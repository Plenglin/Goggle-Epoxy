package io.github.plenglin.goggleapp.weather

import io.github.plenglin.goggle.util.activity.Activity
import io.github.plenglin.goggle.util.format
import io.github.plenglin.goggle.util.input.ButtonInputEvent
import io.github.plenglin.goggle.util.input.EncoderInputEvent
import org.slf4j.LoggerFactory
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.time.format.TextStyle
import java.util.*

class WeatherSummaryActivity(val wctx: WeatherContext) : Activity() {

    private lateinit var buf: BufferedImage
    private val forecastBuf = BufferedImage(256, 30, BufferedImage.TYPE_BYTE_BINARY)
    private var forecastWidth = 0

    private val log = LoggerFactory.getLogger(javaClass)

    private var forecastXOffset = 0

    private lateinit var g: Graphics2D

    override fun start() {
        g = ctx.display.createGraphics()
        buf = BufferedImage(ctx.display.displayWidth, ctx.display.displayHeight, BufferedImage.TYPE_BYTE_BINARY)
    }

    override fun resume() {
        ctx.input.listener = {
            when (it) {
                is EncoderInputEvent -> {
                    forecastXOffset = (forecastXOffset - it.delta * 10).coerceIn(ctx.display.displayWidth - forecastWidth, 0)
                    redraw()
                }
                ButtonInputEvent("x", true) -> {
                    WeatherResources.getForecastData(wctx.lat, wctx.lon) {
                        ctx.activity.pushActivity(TemperatureGraphActivity(it!!))
                    }
                }
                ButtonInputEvent("z", true) -> {
                    ctx.activity.swapActivity(WeatherLoadingActivity())
                }
                ButtonInputEvent("h", true) -> {
                    ctx.activity.popActivity()
                }
            }
        }
        redraw()
    }

    override fun stop() {
        g.dispose()
    }

    private fun redraw() {
        renderForecastData()
        renderWeatherData()
        g.drawImage(buf, 0, 0, null)
        g.drawImage(forecastBuf, forecastXOffset, 38, null)
    }

    private fun renderWeatherData() {
        val (dat, _, lat, lon) = wctx
        val gb = buf.createGraphics()
        gb.font = ctx.resources.fontMedium
        gb.drawString(dat.name, 4, 12)
        gb.font = ctx.resources.fontSmall
        gb.drawString("${dat.main.temp}[${ctx.hardware.therm.temperature}] C", 4, 20)
        gb.drawString("${dat.main.pressure / 10}[${ctx.hardware.bar.pressure}] kPa", 4, 28)
        gb.drawString("${dat.main.humidity}% humid", 4, 36)
        gb.drawString(dat.weather.joinToString(", ") { it.main }, 64, 20)
        gb.drawString("${lat.format(2)} ${if (lat > 0) "N" else "S"}, ${lon.format(2)} ${if (lon > 0) "E" else "W"}", 64, 28)
        gb.drawString(when {
            dat.rain != null -> "${dat.rain.last3}cm rain"
            dat.snow != null -> "${dat.snow.last3}cm snow"
            else -> "No precipitation"
        }, 64, 36)
        gb.dispose()
    }

    private fun renderForecastData() {
        val dat = wctx.forecast
        val gb = forecastBuf.createGraphics()

        val days = dat.days
        log.debug("Sorted days into {}", days)
        var x = gb.drawForDay(0, "Today", days[0]) + 5
        days.drop(1).forEach { pt ->
            x += gb.drawForDay(x, pt.day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()), pt) + 5
        }
        forecastWidth = x - 5
    }

    private fun Graphics2D.drawForDay(x: Int, title: String, dat: DayWeatherData): Int {
        log.debug("Drawing {} to {} ({})", title, x, dat)

        font = ctx.resources.fontSmall
        val metrics = fontMetrics
        val range = "${dat.lowTemp.format(0)}~${dat.highTemp.format(0)} C"
        val conditions = if (dat.conditions.size > 1) {
            dat.conditions.joinToString { it.replace(Regex("[aeiou]"), "") }
        } else {
            dat.conditions[0]
        }

        drawString(title, x, metrics.height - metrics.descent)
        drawString(conditions, x, metrics.height * 2 - metrics.descent)
        drawString(range, x, metrics.height * 3 - metrics.descent)

        return metrics.stringWidth(listOf(range, title, conditions).maxBy { it.length })
    }

}