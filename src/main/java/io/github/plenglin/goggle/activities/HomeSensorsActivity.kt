package io.github.plenglin.goggle.activities

import io.github.plenglin.goggle.activities.HomeSensorsData.LAT_COUNT
import io.github.plenglin.goggle.activities.HomeSensorsData.LONGITUDE_POINTS
import io.github.plenglin.goggle.activities.HomeSensorsData.LONG_COUNT
import io.github.plenglin.goggle.activities.HomeSensorsData.LONG_MAT_WIDTH
import io.github.plenglin.goggle.util.activity.Activity
import org.apache.commons.math3.linear.MatrixUtils
import sun.font.FontFamily
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D

class HomeSensorsActivity : Activity() {

    private lateinit var g: Graphics2D

    override fun start() {
        g = ctx.hardware.display.createGraphics()
        //g.font = Font.createFont(Font.)
    }

    override fun resume() {
        g.stroke = BasicStroke()
    }

    override fun update(dt: Int) {
        val temp = ctx.hardware.therm.temperature
        val pres = ctx.hardware.bar.pressure
        val alt = ctx.hardware.alt.altitude

        val ori = ctx.orientation.orientation
        val matProj = MatrixUtils.createRealMatrix(ori.matrix).scalarMultiply(64.0)
        val trLongitude = matProj.multiply(LONGITUDE_POINTS)
        g.clearRect(0, 0, 128, 128)

        for (i in 0 until LONG_COUNT) {
            val j = 2 * i
            val a = trLongitude.getColumn(j)
            if (a[2] > 0) {
                val b = trLongitude.getColumn(j + 1)
                println("${a.contentToString()}; ${b.contentToString()}")
                g.drawLine(a[0].toInt() + 64, a[1].toInt() + 32, b[0].toInt() + 64, b[1].toInt() + 32)
            }
        }

        g.font = Font("Arial", Font.PLAIN, 8)

        drawAtPoint(trLongitude.getColumn(LONG_MAT_WIDTH)) {
            g.drawString("W", it[0].toInt() + 64, it[1].toInt() + 32)
        }
        drawAtPoint(trLongitude.getColumn(LONG_MAT_WIDTH + 1)) {
            g.drawString("N", it[0].toInt() + 64, it[1].toInt() + 32)
        }

        g.drawString("$temp°C", 0, 9)
        g.drawString("$pres kPa", 0, 18)
        g.drawString("$alt m", 0, 27)
    }

    override fun stop() {
        g.dispose()
    }

    private inline fun drawAtPoint(pt: DoubleArray, action: (DoubleArray) -> Unit) {
        if (pt[2] > 0) {
            action(pt)
        }
    }

}

object HomeSensorsData {

    const val LONG_INCREMENT: Int = 30
    const val LONG_COUNT: Int = 360 / LONG_INCREMENT
    const val LONG_MAT_WIDTH: Int = LONG_COUNT * 2
    const val LONG_LENGTH: Double = 0.2

    const val LAT_INCREMENT: Int = 15
    const val LAT_COUNT: Int = 360 / LAT_INCREMENT
    const val LAT_MAT_WIDTH: Int = LAT_COUNT * 2
    const val LAT_LENGTH: Double = 0.1

    /**
     * 3-vectors embedded in a nx3 matrix. Last 4 vectors represent WNES in that order.
     */
    val LONGITUDE_POINTS by lazy {
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
     * 3-vectors embedded in a nx3 matrix. Last 2 vectors represent up and down in that order.
     */
    val LATITUDE_POINTS by lazy {
        val m = MatrixUtils.createRealMatrix(4, LAT_COUNT * 2)
        for (i in 0 until LAT_COUNT) {
            val a = Math.toRadians((i * LAT_INCREMENT).toDouble())
            m.setColumn(i, doubleArrayOf(Math.cos(a), LAT_LENGTH, Math.sin(a), 1.0))
            m.setColumn(i + 1, doubleArrayOf(Math.cos(a), -LAT_LENGTH, Math.sin(a), 1.0))
        }
        m.setColumn(LAT_MAT_WIDTH, doubleArrayOf(0.0, 1.0, 0.0))
        m.setColumn(LAT_MAT_WIDTH + 1, doubleArrayOf(0.0, -1.0, 0.0))
        m
    }

}