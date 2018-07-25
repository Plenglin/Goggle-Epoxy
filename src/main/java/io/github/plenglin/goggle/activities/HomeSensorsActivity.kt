package io.github.plenglin.goggle.activities

import io.github.plenglin.goggle.util.activity.Activity
import io.github.plenglin.goggle.util.input.ButtonInputEvent
import io.github.plenglin.goggle.util.input.InputEvent
import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.RealMatrix
import java.awt.BasicStroke
import java.awt.Graphics2D
import java.awt.RenderingHints

class HomeSensorsActivity : Activity() {

    private lateinit var g: Graphics2D

    private fun onInput(e: InputEvent) {
        when (e) {
            ButtonInputEvent("z", true) -> {
                ctx.activity.popActivity()
            }
        }
    }

    override fun start() {
        g = ctx.hardware.display.createGraphics()
        //g.font = Font.createFont(Font.)
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

        val ori = ctx.orientation.orientation
        val matProj = MatrixUtils.createRealMatrix(ori.matrix).scalarMultiply(128.0)
        val trLongitude = matProj.multiply(LONGITUDE_POINTS)

        val trLatitude = LATITUDE_POINTS.map { matProj.multiply(it) }
        g.clearRect(0, 0, 128, 128)

        for (i in 0 until LONG_COUNT) {
            val j = 2 * i
            drawAtPoint(trLongitude.getColumn(j)) { a ->
                val b = trLongitude.getColumn(j + 1)
                g.drawLine(a[0].toInt() + 64, a[1].toInt() + 32, b[0].toInt() + 64, b[1].toInt() + 32)
            }
        }

        trLatitude.forEach {
            for (i in 0 until LAT_Y_COUNT) {
                val j = 2 * i
                drawAtPoint(it.getColumn(j)) { a ->
                    val b = it.getColumn(j + 1)
                    g.drawLine(a[0].toInt() + 64, a[1].toInt() + 32, b[0].toInt() + 64, b[1].toInt() + 32)
                }
            }
        }

        g.font = ctx.resources.font

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
        }
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

        const val LAT_Y_INCREMENT: Int = 15
        const val LAT_X_INCREMENT: Int = 45
        const val LAT_Y_COUNT: Int = 360 / LAT_Y_INCREMENT
        const val LAT_X_COUNT: Int = 180 / LAT_X_INCREMENT
        const val LAT_MAT_WIDTH: Int = LAT_X_COUNT * LAT_Y_COUNT * 2
        const val LAT_LENGTH: Double = 0.005

        /**
         * 3-vectors embedded in a nx3 matrix. Last 4 vectors represent ENWS in that order.
         */
        val LONGITUDE_POINTS: RealMatrix by lazy {
            val m = MatrixUtils.createRealMatrix(3, LONG_COUNT * 2 + 4)
            for (i in 0 until LONG_COUNT) {
                val j = i * 2
                val a = Math.toRadians((i * LONG_INCREMENT).toDouble())
                val cos = Math.cos(a)
                val sin = Math.sin(a)
                m.setColumn(j, doubleArrayOf(cos, LONG_LENGTH, sin))
                m.setColumn(j + 1, doubleArrayOf(cos, -LONG_LENGTH, sin))
            }
            m.setColumn(LONG_MAT_WIDTH, doubleArrayOf(1.0, 0.0, 0.0))
            m.setColumn(LONG_MAT_WIDTH + 1, doubleArrayOf(0.0, 0.0, 1.0))
            m.setColumn(LONG_MAT_WIDTH + 2, doubleArrayOf(-1.0, 0.0, 0.0))
            m.setColumn(LONG_MAT_WIDTH + 3, doubleArrayOf(0.0, 0.0, -1.0))
            m
        }

        /**
         * 3-vectors embedded in a nx3 matrices.
         */
        val LATITUDE_POINTS: List<RealMatrix> by lazy {
            val mBase = MatrixUtils.createRealMatrix(3, LAT_MAT_WIDTH)
            for (i in 0 until LAT_Y_COUNT) {
                val k = 2 * i
                val a = Math.toRadians((i * LAT_Y_INCREMENT).toDouble())
                val cos = Math.cos(a)
                val sin = Math.sin(a)
                mBase.setColumn(k, doubleArrayOf(LAT_LENGTH, cos, sin))
                mBase.setColumn(k + 1, doubleArrayOf(-LAT_LENGTH, cos, sin))
            }
            val others = (1 until LAT_X_COUNT).map { i ->
                val a = Math.toRadians((i * LAT_X_INCREMENT).toDouble())
                val cos = Math.cos(a)
                val sin = Math.sin(a)
                MatrixUtils.createRealMatrix(arrayOf(
                        doubleArrayOf(cos, 0.0, sin),
                        doubleArrayOf(0.0, 1.0, 0.0),
                        doubleArrayOf(sin, 0.0, -cos)
                )).multiply(mBase)
            }
            listOf(mBase) + others
        }

    }
}