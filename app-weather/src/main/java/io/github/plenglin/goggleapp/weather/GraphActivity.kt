package io.github.plenglin.goggleapp.weather

import io.github.plenglin.goggle.DoublePair
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

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)

        enum class GraphMode(val l: String, val f: (OWMForecastPoint) -> Double, val range: (DoublePair) -> IntProgression) {
            TEMPERATURE("temp", { it.main.temp }, { (min, max) ->
                (min.roundToInt() / 10 * 10)..(max.roundToInt() / 10 * 10 + 5) step 10
            }),
            PRESSURE("pres",{ it.main.pressure }, { (min, max) ->
                (min.roundToInt())..(max.roundToInt()) step 1
            }),
            HUMIDITY("hum", { it.main.humidity }, { 0..100 step 20 })
        }
    }

    private var iMode = 0
    private val modes = GraphMode.values()

    private lateinit var graphBuf: BufferedImage
    private lateinit var g: Graphics2D
    private var iOffset = 0

    private lateinit var yIndices: IntProgression

    private fun updateYIndices(ys: List<Double>) {
        val mode = modes[iMode]
        yIndices = mode.range(ys.min()!! to ys.max()!!)
    }

    override fun start() {
        g = ctx.display.createGraphics()
        graphBuf = BufferedImage(ctx.display.displayWidth - 16, ctx.display.displayHeight - 16, BufferedImage.TYPE_BYTE_BINARY)
    }

    override fun resume() {
        updateYIndices(data.list.map(modes[iMode].f))
        ctx.input.listener = {
            when (it) {
                is EncoderInputEvent -> {
                    iOffset = (iOffset + it.delta).coerceIn(0, data.cnt - 8)
                    redraw()
                }
                ButtonInputEvent("h", true) -> {
                    ctx.activity.popActivity()
                }
                ButtonInputEvent("x", true) -> {
                    iMode = (iMode + 1) % modes.size
                    updateYIndices(data.list.map(modes[iMode].f))
                    redraw()
                }
            }
        }
        redraw()
    }

    private fun redraw() {
        g.clearRect(ctx.display.displayBounds)
        val mode = modes[iMode]
        val ds = data.list.drop(iOffset).take(8)
        val ys = ds.map(mode.f)
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
        g.drawImage(graphBuf, 0, 16, null)
        g.drawString("${mode.l}: $dateFmt", 0, 14)
    }

    override fun suspend() {

    }

    override fun stop() {
        g.dispose()
    }

}
