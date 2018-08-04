package io.github.plenglin.goggleapp.weather

import io.github.plenglin.goggle.commands.RunnableCommand
import io.github.plenglin.goggle.commands.WaitCommand
import io.github.plenglin.goggle.util.activity.Activity
import io.github.plenglin.goggle.util.clearRect
import java.awt.Graphics2D

class WeatherLoadingActivity : Activity() {

    private lateinit var g: Graphics2D
    private var currentData: OWMCurrentData? = null
    private var forecastData: OWMForecastData? = null

    override fun start() {
        g = ctx.display.createGraphics()
    }

    override fun resume() {
        val lat = ctx.hardware.gps.latitude
        val lon = ctx.hardware.gps.longitude

        g.clearRect(ctx.display.displayBounds)
        g.font = ctx.resources.fontLarge
        g.fontMetrics.let {
            g.drawString(
                    "Loading...",
                    (ctx.display.displayWidth - it.stringWidth("Loading...")) / 2,
                    (ctx.display.displayHeight + it.height) / 2)
        }
        WeatherResources.getCurrentWeatherData(lat, lon) {
            currentData = it
        }

        WeatherResources.getForecastData(lat, lon) {
            forecastData = it
        }
        ctx.scheduler.addCommand(WaitCommand(
                ctx.scheduler,
                RunnableCommand {
                    ctx.activity.swapActivity(WeatherSummaryActivity(WeatherContext(currentData!!, forecastData!!, lat, lon)))
                }) { currentData != null && forecastData != null }
        )
    }

    override fun suspend() {
        currentData = null
        forecastData = null
    }

    override fun stop() {
        g.dispose()
    }

}