package io.github.plenglin.goggleapp.weather

import io.github.plenglin.goggle.IntPair
import io.github.plenglin.goggle.util.activity.Activity
import io.github.plenglin.goggle.util.clearRect
import io.github.plenglin.goggle.util.input.ButtonInputEvent
import io.github.plenglin.goggle.util.input.EncoderInputEvent
import io.github.plenglin.goggle.util.lerp
import io.github.plenglin.goggle.util.lerper
import org.slf4j.LoggerFactory
import java.awt.BasicStroke
import java.awt.Graphics2D
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class TemperatureGraphActivity(val data: OWMForecastData) : Activity() {

    private val log = LoggerFactory.getLogger(javaClass)
    private lateinit var g: Graphics2D
    private var iOffset = 0

    override fun start() {
        g = ctx.display.createGraphics()
    }

    override fun resume() {
        ctx.input.listener = {
            when (it) {
                is EncoderInputEvent -> {
                    iOffset = (iOffset + it.delta).coerceIn(0, data.cnt - 8)
                    redraw()
                }
                ButtonInputEvent("h", true) -> {
                    ctx.activity.popActivity()
                }
            }
        }
        redraw()
    }

    private fun redraw() {
        g.clearRect(ctx.display.displayBounds)
        val ds = data.list.drop(iOffset).take(8)

        val graphWidth = ctx.display.displayWidth - 12
        val hSpacing = graphWidth / 8
        val yAxTop = 14
        val yAxBot = ctx.display.displayHeight - 8

        val tempMin = (ds.map { it.main.temp }.min()!!.toInt() - 10) / 10 * 10
        val tempMax = (ds.map { it.main.temp }.max()!!.toInt() + 10) / 10 * 10
        val tempToYCoord = lerper(tempMin, tempMax, yAxBot, yAxTop)
        val minDay = Calendar.getInstance().apply { time = Date(ds.map { it.dt }.min()!! * 1000) }
        log.debug("hSpacing = {}, tempMin = $tempMin, tempMax = $tempMax", hSpacing)

        g.font = ctx.resources.fontMedium
        g.drawString(SimpleDateFormat("yyyy-MM-dd").format(minDay.time), 0, 14)
        g.font = ctx.resources.fontSmall
        val smallMetrics = g.fontMetrics
        val pts = mutableListOf<IntPair>()
        ds.forEachIndexed { i, pt ->
            val cal = Calendar.getInstance().apply {
                time = Date(pt.dt * 1000)
            }
            val hr = cal.get(Calendar.HOUR_OF_DAY)
            val x = i * hSpacing + 14
            val label = "${hr}h"
            log.debug("$i: hr = $hr; x = $x")
            g.drawString(label, x - smallMetrics.stringWidth(label) / 2, ctx.display.displayHeight)
            pts.add(x to tempToYCoord(pt.main.temp.roundToInt()))
        }

        g.stroke = BasicStroke(1f)
        g.drawLine(12, yAxTop, 12, yAxBot)
        g.drawLine(ctx.display.displayWidth - 4, yAxBot, 12, yAxBot)

        g.stroke = BasicStroke(1.3f)

        var (x1, y1) = pts.first()
        for ((x2, y2) in pts.drop(1)) {
            g.drawLine(x1, y1, x2, y2)
            x1 = x2
            y1 = y2
        }
        for (t in tempMin..tempMax step 10) {
            g.drawString(t.toString(), 0, tempToYCoord(t))
        }
    }

    override fun suspend() {

    }

    override fun stop() {
        g.dispose()
    }

}