package io.github.plenglin.goggle.activities

import io.github.plenglin.goggle.util.activity.Activity
import io.github.plenglin.goggle.util.input.ButtonInputEvent
import io.github.plenglin.goggle.util.input.InputEvent
import io.github.plenglin.goggle.util.space.Line
import io.github.plenglin.goggle.util.space.OrthographicCamera
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.apache.commons.math3.linear.MatrixUtils
import java.awt.BasicStroke
import java.awt.Graphics2D
import java.awt.RenderingHints
import kotlin.math.roundToInt

class HomeSensorsActivity : Activity() {

    private lateinit var g: Graphics2D
    private val cam = OrthographicCamera()

    private fun onInput(e: InputEvent) {
        when (e) {
            ButtonInputEvent("z", true) -> {
                ctx.activity.popActivity()
            }
        }
    }

    override fun start() {
        g = ctx.hardware.display.createGraphics()
        //g.fontPrimary = Font.createFont(Font.)
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

        cam.rotation = ctx.orientation.orientation
        cam.translation = Vector3D(64.0, 32.0, 0.0)
        cam.scale = 128.0
        cam.update()

        val trLongitude = LONGITUDE_LINES.map { cam.project(it.first) to cam.project(it.second) }
        val trLatitude = LATITUDE_LINES.map { cam.project(it.first) to cam.project(it.second) }

        g.clearRect(0, 0, 128, 128)

        for ((a, b) in trLongitude + trLatitude) {
            drawAtPoint(a) {
                g.drawLine(a[0].roundToInt(), a[1].roundToInt(), b[0].roundToInt(), b[1].roundToInt())
            }
        }

        g.font = ctx.resources.fontPrimary
        val metrics = g.fontMetrics

        SYMBOLS.forEach { (s, p) ->
            drawAtPoint(cam.project(p)) {
                val x = metrics.stringWidth(s)
                val y = metrics.height
                g.drawString(s, it[0].roundToInt() - x / 2, it[1].roundToInt() + y / 2)
            }
        }
        /*
        drawAtPoint(trLongitude.getColumn(LONG_MAT_WIDTH)) {
            g.drawString("E", it[0].toInt() + 64, it[1].toInt() + 32)
        }
        drawAtPoint(trLongitude.getColumn(LONG_MAT_WIDTH + 1)) {
            g.drawString("N", it[0].toInt() + 64, it[1].toInt() + 32)
        }
        drawAtPoint(trLongitude.getColumn(LONG_MAT_WIDTH + 2)) {
            g.drawString("W", it[0].toInt() + 64, it[1].toInt() + 32)
        }
        drawAtPoint(trLongitude.getColumn(LONG_MAT_WIDTH + 3)) {
            g.drawString("S", it[0].toInt() + 64, it[1].toInt() + 32)
        }*/
        //println()

        g.drawString("${"%.1f".format(temp)}C", 0, 48)
        g.drawString("${"%.1f".format(pres)}kPa", 0, 56)
        g.drawString("${"%.1f".format(alt)}m", 0, 64)
    }

    override fun stop() {
        g.dispose()
        ctx.input.listener = {}
    }

    private inline fun drawAtPoint(pt: DoubleArray, action: (DoubleArray) -> Unit) {
        if (pt[2] > 0) {
            action(pt)
        }
    }

    companion object {

        const val LONG_INCREMENT: Int = 15
        const val LONG_COUNT: Int = 360 / LONG_INCREMENT
        const val LONG_MAT_WIDTH: Int = LONG_COUNT * 2
        const val LONG_LENGTH: Double = 0.01

        const val LAT_Y_INCREMENT: Int = 10
        const val LAT_X_INCREMENT: Int = 45
        const val LAT_Y_COUNT: Int = 360 / LAT_Y_INCREMENT
        const val LAT_X_COUNT: Int = 180 / LAT_X_INCREMENT
        const val LAT_MAT_WIDTH: Int = LAT_X_COUNT * LAT_Y_COUNT * 2
        const val LAT_LENGTH: Double = 0.005

        /**
         * 3-vectors embedded in a nx3 matrix. Last 4 vectors represent ENWS in that order.
         */
        val LONGITUDE_LINES: List<Line> by lazy {
            (0 until LONG_COUNT).map { i ->
                val a = Math.toRadians((i * LONG_INCREMENT).toDouble())
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
            val base = (0 until LAT_Y_COUNT).map { i ->
                val a = Math.toRadians((i * LAT_Y_INCREMENT).toDouble())
                val cos = Math.cos(a)
                val sin = Math.sin(a)
                doubleArrayOf(LAT_LENGTH, cos, sin) to doubleArrayOf(-LAT_LENGTH, cos, sin)
            }
            val out = mutableListOf<Line>()
            out.addAll(base)
            (1 until LAT_X_COUNT).forEach { i ->
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

        val INV_SQRT_2 = 1 / Math.sqrt(2.0)

        val SYMBOLS: List<Pair<String, DoubleArray>> = listOf(
                "N" to doubleArrayOf(0.0, 0.0, 1.0),
                "NE" to doubleArrayOf(INV_SQRT_2, 0.0, INV_SQRT_2),
                "E" to doubleArrayOf(1.0, 0.0, 0.0),
                "SE" to doubleArrayOf(INV_SQRT_2, 0.0, -INV_SQRT_2),
                "S" to doubleArrayOf(0.0, 0.0, -1.0),
                "SW" to doubleArrayOf(-INV_SQRT_2, 0.0, -INV_SQRT_2),
                "W" to doubleArrayOf(-1.0, 0.0, 0.0),
                "NW" to doubleArrayOf(-INV_SQRT_2, 0.0, INV_SQRT_2),
                "UP" to doubleArrayOf(0.0, 1.0, 0.0),
                "DN" to doubleArrayOf(0.0, -1.0, 0.0)
        )
    }
}