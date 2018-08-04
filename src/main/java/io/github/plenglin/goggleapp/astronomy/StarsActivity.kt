package io.github.plenglin.goggleapp.astronomy

import io.github.plenglin.goggle.STD_AXES_TO_ORI
import io.github.plenglin.goggle.commands.PeriodicCommand
import io.github.plenglin.goggle.commands.RunnableCommand
import io.github.plenglin.goggle.util.activity.Activity
import io.github.plenglin.goggle.util.input.ButtonInputEvent
import io.github.plenglin.goggle.util.input.EncoderInputEvent
import io.github.plenglin.goggle.util.space.PerspectiveCamera
import org.apache.commons.math3.geometry.euclidean.threed.Rotation
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.Graphics2D
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.roundToInt

class StarsActivity : Activity() {
    private val log = LoggerFactory.getLogger(javaClass)

    private lateinit var g: Graphics2D
    private lateinit var offset: Rotation

    private val cam = PerspectiveCamera()

    private var endIndex = 0
    private var appMag = 30

    private var stopDisplayMagnitudeAfter = 0L

    private lateinit var offsetUpdater: PeriodicCommand

    private fun updateApparentMagnitude() {
        endIndex = AstronomyResources.stars.indexOfLast { it.apparentMagnitude <= appMag / 10.0 }
    }

    override fun start() {
        g = ctx.hardware.display.createGraphics()
    }

    override fun resume() {
        updateApparentMagnitude()
        offset = getOffset()

        stopDisplayMagnitudeAfter = System.currentTimeMillis() + 1000L
        offsetUpdater = PeriodicCommand(
                ctx.scheduler,
                RunnableCommand {
                    log.debug("Updating offset")
                    offset = getOffset()
                }, 100000L, 100000L
        )
        ctx.scheduler.addCommand(offsetUpdater)

        ctx.input.listener = {
            when (it) {
                ButtonInputEvent("h", true) -> ctx.activity.popActivity()
                is EncoderInputEvent -> {
                    appMag = minOf(maxOf(appMag + it.delta, -10), 70)
                    stopDisplayMagnitudeAfter = System.currentTimeMillis() + 1000L
                    updateApparentMagnitude()
                }
            }
        }
        log.debug("We have {} stars: ", AstronomyResources.stars.size, AstronomyResources.stars)
    }

    //private val alderamin = AstronomyResources.stars.find { it.name == "Alderamin" }!!

    override fun update(dt: Int) {
        val time = System.currentTimeMillis()
        val ori = ctx.orientation.orientation

        cam.translation = Vector3D.ZERO
        cam.postTranslation = Vector2D(64.0, 32.0)
        cam.postScale = 128.0
        cam.rotation = ori.applyTo(offset)
        cam.projectionRadiusX = 0.5
        cam.projectionRadiusY = 0.2
        cam.update()

        g.clearRect(0, 0, ctx.hardware.display.displayWidth, ctx.hardware.display.displayHeight)
        g.color = Color.white

        for (i in 0 until endIndex) {
            val star = AstronomyResources.stars[i]
            cam.draw(star.cSpherePosition) {
                val (x, y) = it[0]
                log.debug("Drawing to {}, {}: {}", x, y, star)
                g.fillRect(x.roundToInt(), y.roundToInt(), 1, 1)
            }
        }

        cam.draw(doubleArrayOf(0.0, 0.0, 1.0)) {
            val (x, y) = it[0]
            g.drawString("CN", x.roundToInt(), y.roundToInt())
        }

        cam.draw(doubleArrayOf(0.0, 0.0, -1.0)) {
            val (x, y) = it[0]
            g.drawString("CS", x.roundToInt(), y.roundToInt())
        }

        cam.rotation = ori
        cam.update()

        g.font = ctx.resources.fontSmall
        val metrics = g.fontMetrics

        AstronomyResources.SYMBOLS.forEach { (s, p) ->
            cam.draw(p) {
                val (x, y) = it[0]
                val dx = metrics.stringWidth(s) / 2
                val dy = metrics.height / 2
                g.drawString(s, x.roundToInt() - dx, y.roundToInt() + dy)
            }
        }

        if (time < stopDisplayMagnitudeAfter) {
            val s = appMag.toString()
            g.drawString("AppMag: " + s.dropLast(1) + "." + s.last(), 0, ctx.hardware.display.displayHeight - metrics.descent)
        }

    }

    private fun getOffset(): Rotation {
        // Calculate angle to equinox by getting time passed since equinox
        val now = LocalDateTime.now(Clock.systemUTC())
        val equinox = LocalDateTime.of(now.year, 3, 20, 0, 0, 0)

        val equinoxDifference = now.toEpochSecond(ZoneOffset.UTC) - equinox.toEpochSecond(ZoneOffset.UTC)
        val angleToEquinox = 2 * Math.PI * equinoxDifference / AstronomyResources.SECONDS_PER_YEAR

        // Correct a2e projected onto plane normal to earth's axis, since it is distorted by earth's axial tilt
        val adjAngleToEquinox = Math.atan2(
                AstronomyResources.EARTH_PROJECTION_DISTORTION * Math.sin(angleToEquinox),
                -Math.cos(angleToEquinox)
        )

        // Calculate angle to midnight by getting time passed since midnight (UTC, of course)
        val timeOfDay = now.hour * 3600 + now.minute * 60 + now.second
        val angleToMidnight = 2 * Math.PI * timeOfDay / AstronomyResources.SECONDS_PER_DAY

        val ra = ctx.hardware.gps.longitudeInRadians + adjAngleToEquinox + angleToMidnight - Math.PI
        val dec = ctx.hardware.gps.latitudeInRadians

        log.debug("a2e: {}; aa2e: {}; a2m: {}; RA: {}; Dec: {}", angleToEquinox, adjAngleToEquinox, angleToMidnight, ra, dec)

        return STD_AXES_TO_ORI.applyTo(Rotation(RotationOrder.XYZ, 0.0, Math.PI / 2 - dec, Math.PI / 2 - ra))
    }

    override fun suspend() {
        offsetUpdater.stopExecution()
    }

    override fun stop() {
        g.dispose()
    }
}
