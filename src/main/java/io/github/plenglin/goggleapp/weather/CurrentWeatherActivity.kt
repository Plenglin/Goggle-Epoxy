package io.github.plenglin.goggleapp.weather

import io.github.plenglin.goggle.commands.RunnableCommand
import io.github.plenglin.goggle.commands.WaitCommand
import io.github.plenglin.goggle.util.activity.Activity
import io.github.plenglin.goggle.util.format
import io.github.plenglin.goggle.util.input.ButtonInputEvent
import io.github.plenglin.goggle.util.input.EncoderInputEvent
import org.slf4j.LoggerFactory
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.time.format.TextStyle
import java.util.*

class CurrentWeatherActivity : Activity() {

    private lateinit var buf: BufferedImage
    private val forecastBuf = BufferedImage(256, 30, BufferedImage.TYPE_BYTE_BINARY)
    private var forecastWidth = 0

    private val log = LoggerFactory.getLogger(javaClass)

    private var currentDataReady = false
    private var forecastDataReady = false
    private var forecastXOffset = 0

    private lateinit var g: Graphics2D

    override fun start() {
        g = ctx.display.createGraphics()
        buf = BufferedImage(ctx.display.displayWidth, ctx.display.displayHeight, BufferedImage.TYPE_BYTE_BINARY)
    }

    override fun resume() {
        g.clearRect(0, 0, ctx.display.displayWidth, ctx.display.displayHeight)

        g.font = ctx.resources.fontLarge
        g.fontMetrics.let {
            g.drawString(
                    "Loading...",
                    (ctx.display.displayWidth - it.stringWidth("Loading...")) / 2,
                    (ctx.display.displayHeight + it.height) / 2)
        }

        log.debug("Clearing ready flags")
        currentDataReady = false
        forecastDataReady = false

        val lat = ctx.hardware.gps.latitude
        val lon = ctx.hardware.gps.longitude

        WeatherResources.getCurrentWeatherData(lat, lon) {
            log.debug("Rendering weather data")
            renderWeatherData(lat, lon, it!!)
            currentDataReady = true
        }

        WeatherResources.getForecastData(lat, lon) {
            renderForecastData(it!!)
            forecastDataReady = true
        }

        ctx.scheduler.addCommand(WaitCommand(
                ctx.scheduler,
                RunnableCommand {
                    redraw()
                }) { currentDataReady && forecastDataReady }
        )

        ctx.input.listener = {
            when (it) {
                is EncoderInputEvent -> {
                    forecastXOffset = (forecastXOffset - it.delta * 10).coerceIn(ctx.display.displayWidth - forecastWidth, 0)
                    redraw()
                }
                ButtonInputEvent("h", true) -> {
                    ctx.activity.popActivity()
                }
            }
        }
    }

    override fun stop() {
        g.dispose()
    }

    private fun redraw() {
        g.drawImage(buf, 0, 0, null)
        g.drawImage(forecastBuf, forecastXOffset, 38, null)
    }

    private fun renderWeatherData(lat: Double, lon: Double, dat: OWMCurrentData) {
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

    private fun renderForecastData(dat: OWMForecastData) {
        val gb2 = forecastBuf.createGraphics()

        val days = dat.days
        log.debug("Sorted days into {}", days)
        var x = gb2.drawForDay(0, "Today", days[0]) + 5
        days.drop(1).forEach { pt ->
            x += gb2.drawForDay(x, pt.day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()), pt) + 5
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