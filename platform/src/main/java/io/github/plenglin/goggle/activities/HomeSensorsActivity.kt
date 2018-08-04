package io.github.plenglin.goggle.activities

import io.github.plenglin.goggle.util.activity.Activity
import io.github.plenglin.goggle.util.input.ButtonInputEvent
import io.github.plenglin.goggle.util.input.InputEvent
import io.github.plenglin.goggle.util.space.Line
import io.github.plenglin.goggle.util.space.PerspectiveCamera
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D
import org.apache.commons.math3.linear.MatrixUtils
import org.slf4j.LoggerFactory
import java.awt.BasicStroke
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

class HomeSensorsActivity : Activity() {

    private lateinit var g: Graphics2D
    private val log = LoggerFactory.getLogger(javaClass)
    private val cam = PerspectiveCamera()

    private fun onInput(e: InputEvent) {
        when (e) {
            ButtonInputEvent("z", true) -> {
                ctx.activity.popActivity()
            }
            ButtonInputEvent("h", true) -> {
                ctx.activity.pushActivity(AppListingActivity())
            }
        }
    }

    override fun start() {
        g = ctx.hardware.display.createGraphics()
    }

    override fun resume() {
        ctx.input.listener = this::onInput
        g.stroke = BasicStroke(1.2f)
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF)
    }

    override fun update(dt: Int) {
        val temp = ctx.hardware.therm.temperature
        val pres = ctx.hardware.bar.pressure
        val alt = ctx.hardware.alt.altitude
        val time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        cam.rotation = ctx.orientation.orientation
        cam.translation = Vector3D.ZERO
        cam.postTranslation = Vector2D(64.0, 32.0)
        cam.postScale = 128.0
        cam.projectionRadiusX = 0.5
        cam.projectionRadiusY = 0.2

        cam.update()

        g.clearRect(0, 0, 128, 128)
        val oldTform = g.transform
        /*g.translate(64, 32)
        g.scale(64.0, 64.0)*/
        log.debug("{}", g.transform)

        for ((a, b) in LONGITUDE_LINES + LATITUDE_LINES) {
            cam.draw(a, b) { pts ->
                log.debug("{}", pts)
                g.drawLine(
                        pts[0].first.roundToInt(), pts[0].second.roundToInt(),
                        pts[1].first.roundToInt(), pts[1].second.roundToInt())
            }
        }

        g.font = ctx.resources.fontSmall
        val metrics = g.fontMetrics

        SYMBOLS.forEach { (s, p) ->
            cam.draw(p) {
                val x = metrics.stringWidth(s)
                val y = metrics.height
                g.drawString(s, it[0].first.roundToInt() - x / 2, it[0].second.roundToInt() + y / 2)
            }
        }

        g.transform = oldTform
        g.drawString(time, ctx.display.displayWidth * 6/8, metrics.height)
        g.drawString("${"%.1f".format(temp)}C", 0, 48)
        g.drawString("${"%.1f".format(pres)}kPa", 0, 56)
        g.drawString("${"%.1f".format(alt)}m", 0, 64)
    }

    override fun stop() {
        g.dispose()
    }

    companion object {

        const val LONG_INCREMENT: Int = 15
        const val LONG_LENGTH: Double = 0.01

        const val LAT_Y_INCREMENT: Int = 10
        const val LAT_X_INCREMENT: Int = 45
        const val LAT_LENGTH: Double = 0.005

        /**
         * 3-vectors embedded in a nx3 matrix.
         */
        val LONGITUDE_LINES: List<Line> by lazy {
            (0 until 360 step LONG_INCREMENT)
                    .filter { it % 45 != 0 }
                    .map { d ->
                        val a = Math.toRadians(d.toDouble())
                        val cos = Math.cos(a)
                        val sin = Math.sin(a)
                        val p1 = doubleArrayOf(cos, LONG_LENGTH, sin)
                        val p2 = doubleArrayOf(cos, -LONG_LENGTH, sin)
                        p1 to p2
                    }
        }

        /**
         * 3-vectors embedded in a nx3 matrices.
         */
        val LATITUDE_LINES: List<Line> by lazy {
            val base = (0 until 360 step LAT_Y_INCREMENT)
                    .filter {
                        it % 90 != 0
                    }
                    .map { d ->
                        val a = Math.toRadians(d.toDouble())
                        val cos = Math.cos(a)
                        val sin = Math.sin(a)
                        doubleArrayOf(LAT_LENGTH, cos, sin) to doubleArrayOf(-LAT_LENGTH, cos, sin)
                    }
            val out = mutableListOf<Line>()
            out.addAll(base)
            (LAT_X_INCREMENT until 180 step LAT_X_INCREMENT)
                    .forEach { i ->
                        val a = Math.toRadians((i * LAT_X_INCREMENT).toDouble())
                        val cos = Math.cos(a)
                        val sin = Math.sin(a)

                        val rot = MatrixUtils.createRealMatrix(arrayOf(
                                doubleArrayOf(cos, 0.0, sin),
                                doubleArrayOf(0.0, 1.0, 0.0),
                                doubleArrayOf(sin, 0.0, -cos)
                        ))
                        base.forEach {
                            out.add(rot.multiply(MatrixUtils.createColumnRealMatrix(it.first)).getColumn(0) to
                                    rot.multiply(MatrixUtils.createColumnRealMatrix(it.second)).getColumn(0))
                        }
                    }
            out

        }

        private val INV_SQRT_2 = 1 / Math.sqrt(2.0)

        val SYMBOLS: List<Pair<String, DoubleArray>> = listOf(
                "N" to doubleArrayOf(0.0, 0.0, 1.0),
                "NE" to doubleArrayOf(INV_SQRT_2, 0.0, INV_SQRT_2),
                "E" to doubleArrayOf(1.0, 0.0, 0.0),
                "SE" to doubleArrayOf(INV_SQRT_2, 0.0, -INV_SQRT_2),
                "S" to doubleArrayOf(0.0, 0.0, -1.0),
                "SW" to doubleArrayOf(-INV_SQRT_2, 0.0, -INV_SQRT_2),
                "W" to doubleArrayOf(-1.0, 0.0, 0.0),
                "NW" to doubleArrayOf(-INV_SQRT_2, 0.0, INV_SQRT_2),
                "UP" to doubleArrayOf(0.0, -1.0, 0.0),
                "DN" to doubleArrayOf(0.0, 1.0, 0.0)
        )
    }
}