package io.github.plenglin.goggleapp.astronomy

import io.github.plenglin.goggle.STD_AXES_TO_ORI
import io.github.plenglin.goggle.commands.PeriodicCommand
import io.github.plenglin.goggle.commands.RunCommand
import io.github.plenglin.goggle.util.activity.Activity
import io.github.plenglin.goggle.util.input.ButtonInputEvent
import io.github.plenglin.goggle.util.input.EncoderInputEvent
import io.github.plenglin.goggle.util.space.OrthographicCamera
import org.apache.commons.math3.geometry.euclidean.threed.Rotation
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.Graphics2D
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.roundToInt

class StarsActivity : Activity() {
    var latitude: Double = Math.toRadians(35.0)
    var longitude: Double = Math.toRadians(-122.0)

    private val log = LoggerFactory.getLogger(javaClass.name)

    private lateinit var g: Graphics2D
    private lateinit var offset: Rotation

    private val cam = OrthographicCamera()

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
        cam.scale = 128.0
        cam.translation = Vector3D(64.0, 32.0, 0.0)

        stopDisplayMagnitudeAfter = System.currentTimeMillis() + 1000L
        offsetUpdater = PeriodicCommand(
                ctx.scheduler,
                RunCommand {
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

    override fun update(dt: Int) {
        val time = System.currentTimeMillis()
        val ori = ctx.orientation.orientation
        cam.rotation = ori.applyTo(offset)
        cam.update()

        g.clearRect(0, 0, ctx.hardware.display.displayWidth, ctx.hardware.display.displayHeight)
        g.color = Color.white

        for (i in 0 until endIndex) {
            val star = AstronomyResources.stars[i]
            val pt = cam.project(star.cSpherePosition)
            if (pt[2] > 0) {
                log.trace("Drawing to {}: {}", pt, star)
                g.fillRect(pt[0].roundToInt(), pt[1].roundToInt(), 1, 1)
            }
        }

        cam.project(doubleArrayOf(0.0, 0.0, 1.0)).let {
            if (it[2] > 0) {
                g.drawString("CN", it[0].roundToInt(), it[1].roundToInt())
            }
        }

        cam.project(doubleArrayOf(0.0, 0.0, -1.0)).let {
            if (it[2] > 0) {
                g.drawString("CS", it[0].roundToInt(), it[1].roundToInt())
            }
        }

        cam.rotation = ori
        cam.update()

        g.font = ctx.resources.fontPrimary
        val metrics = g.fontMetrics

        AstronomyResources.SYMBOLS.forEach { (s, p) ->
            cam.project(p).let {
                if (it[2] > 0) {
                    val x = metrics.stringWidth(s)
                    val y = metrics.height
                    g.drawString(s, it[0].roundToInt() - x / 2, it[1].roundToInt() + y / 2)
                }
            }
        }

        if (time < stopDisplayMagnitudeAfter) {
            val s = appMag.toString()
            g.drawString("AppMag: " + s.dropLast(1) + "." + s.last(), 0, ctx.hardware.display.displayHeight - metrics.ascent)
        }

    }

    private fun getOffset(): Rotation {
        val now = LocalDateTime.now(Clock.systemUTC())
        val equinox = LocalDateTime.of(now.year, 3, 20, 0, 0, 0)
        val equinoxDifference = now.toEpochSecond(ZoneOffset.UTC) - equinox.toEpochSecond(ZoneOffset.UTC)
        val angleToEquinox = 2 * Math.PI * equinoxDifference / AstronomyResources.SECONDS_PER_YEAR

        val timeOfDay = now.hour * 3600 + now.minute * 60 + now.second
        val angleToMidnight = 2 * Math.PI * timeOfDay / AstronomyResources.SECONDS_PER_DAY

        val ra = longitude + angleToEquinox + angleToMidnight - 180
        val dec = latitude

        return STD_AXES_TO_ORI.applyTo(Rotation(RotationOrder.XYZ, 0.0, Math.PI / 2 - dec, ra))
    }

    override fun suspend() {
        offsetUpdater.stopExecution()
    }

    override fun stop() {
        g.dispose()
    }
}
