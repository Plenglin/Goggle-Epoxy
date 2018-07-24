package io.github.plenglin.goggle.activities

import io.github.plenglin.goggle.activities.HomeSensorsData.LAT_COUNT
import io.github.plenglin.goggle.activities.HomeSensorsData.LONGITUDE_POINTS
import io.github.plenglin.goggle.util.activity.Activity
import org.apache.commons.math3.linear.MatrixUtils
import java.awt.BasicStroke
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

        for (i in 0 until LAT_COUNT) {
            val a = trLongitude.getColumn(2 * i)
            if (a[2] > 0) {
                val b = trLongitude.getColumn(2 * i + 1)
                g.drawLine(a[0].toInt(), a[1].toInt(), b[0].toInt(), b[1].toInt())
            }
        }

        g.drawString("$temp°C", 0, 0)
        g.drawString("$pres kPa", 0, 12)
        g.drawString("$alt m", 0, 24)
    }

    override fun stop() {
        g.dispose()
    }

}

object HomeSensorsData {

    const val LONG_INCREMENT = 15
    const val LONG_COUNT = 360 / LONG_INCREMENT
    const val LONG_LENGTH = 0.1

    const val LAT_INCREMENT = 15
    const val LAT_COUNT = 360 / LAT_INCREMENT
    const val LAT_LENGTH = 0.1

    /**
     * 3-vectors embedded in a nx3 matrix. Last 4 vectors represent WNES in that order.
     */
    val LONGITUDE_POINTS by lazy {
        val m = MatrixUtils.createRealMatrix(3, LONG_COUNT * 2 + 4)
        for (i in 0 until LONG_COUNT) {
            val a = Math.toRadians((i * LONG_INCREMENT).toDouble())
            m.setColumn(i, doubleArrayOf(Math.cos(a), LONG_LENGTH, Math.sin(a)))
            m.setColumn(i + 1, doubleArrayOf(Math.cos(a), -LONG_LENGTH, Math.sin(a)))
        }
        m.setColumn(LONG_COUNT, doubleArrayOf(1.0, 0.0, 0.0))
        m.setColumn(LONG_COUNT + 1, doubleArrayOf(0.0, 0.0, 1.0))
        m.setColumn(LONG_COUNT + 2, doubleArrayOf(-1.0, 0.0, 0.0))
        m.setColumn(LONG_COUNT + 3, doubleArrayOf(0.0, 0.0, -1.0))
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
        m.setColumn(LAT_COUNT, doubleArrayOf(0.0, 1.0, 0.0))
        m.setColumn(LAT_COUNT + 1, doubleArrayOf(0.0, -1.0, 0.0))
        m
    }

}