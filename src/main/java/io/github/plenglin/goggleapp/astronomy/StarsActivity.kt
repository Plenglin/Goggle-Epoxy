package io.github.plenglin.goggleapp.astronomy

import io.github.plenglin.goggle.util.activity.Activity
import io.github.plenglin.goggle.util.input.ButtonInputEvent
import io.github.plenglin.goggle.util.space.OrthographicCamera
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.Graphics2D
import kotlin.math.roundToInt

class StarsActivity : Activity() {
    var latitude: Double = Math.toRadians(35.0)
    var longitude: Double = Math.toRadians(-122.0)

    private val log = LoggerFactory.getLogger(javaClass.name)

    private lateinit var g: Graphics2D
    private val cam = OrthographicCamera()
    private val stars = AstronomyResources.stars.filter { it.apparentMagnitude < 3.0 }

    override fun start() {
        g = ctx.hardware.display.createGraphics()
        cam.scale = 128.0
        cam.translation = Vector3D(64.0, 32.0, 0.0)
    }

    override fun resume() {
        ctx.input.listener = {
            when (it) {
                ButtonInputEvent("h", true) -> ctx.activity.popActivity()
            }
        }
        log.debug("Working with {} stars: ", stars.size, stars)
    }

    override fun update(dt: Int) {
        val ori = ctx.orientation.orientation
        cam.rotation = ori
        cam.update()

        g.clearRect(0, 0, ctx.hardware.display.displayWidth, ctx.hardware.display.displayHeight)
        g.color = Color.white

        stars.forEach {
            val pt = cam.project(it.cSpherePosition)
            if (pt[2] > 0) {
                log.debug("Drawing to {}: {}", pt, it)
                g.fillRect(pt[0].roundToInt(), pt[1].roundToInt(), 1, 1)
            }
        }

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

    }

    override fun suspend() {

    }
}
