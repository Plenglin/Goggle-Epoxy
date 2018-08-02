package io.github.plenglin.goggleapp.weather

import io.github.plenglin.goggle.util.LineGraph
import io.github.plenglin.goggle.util.activity.Activity
import io.github.plenglin.goggle.util.clearRect
import io.github.plenglin.goggle.util.input.ButtonInputEvent
import io.github.plenglin.goggle.util.input.EncoderInputEvent
import org.slf4j.LoggerFactory
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class GraphActivity(val data: OWMForecastData) : Activity() {

    private val log = LoggerFactory.getLogger(javaClass)

    private lateinit var graphBuf: BufferedImage
    private lateinit var g: Graphics2D
    private var iOffset = 0

    private val yIndices: IntProgression

    init {
        val ys = data.list.map { it.main.temp }
        yIndices = (ys.min()!!.roundToInt() / 10 * 10)..(ys.max()!!.roundToInt() / 10 * 10) step 10
    }

    override fun start() {
        g = ctx.display.createGraphics()
        graphBuf = BufferedImage(ctx.display.displayWidth - 16, ctx.display.displayHeight - 12, BufferedImage.TYPE_BYTE_BINARY)
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
        val ys = ds.map { it.main.temp }
        val minDay = Calendar.getInstance().apply { time = Date(ds.map { it.dt }.min()!! * 1000) }
        val dateFmt = SimpleDateFormat("yyyy-MM-dd").format(minDay.time)
        val lineGraph = LineGraph(
                ds.map {
                    val hr = Calendar.getInstance().apply {
                        time = Date(it.dt * 1000)
                    }.get(Calendar.HOUR_OF_DAY)
                    "${hr}h"
                }, ys,
                yIndices
        )
        lineGraph.drawTo(graphBuf, ctx.resources.fontSmall)
        g.font = ctx.resources.fontMedium
        g.drawImage(graphBuf, 0, 12, null)
        g.drawString("$dateFmt (temp)", 0, 14)
    }

    override fun suspend() {

    }

    override fun stop() {
        g.dispose()
    }

}