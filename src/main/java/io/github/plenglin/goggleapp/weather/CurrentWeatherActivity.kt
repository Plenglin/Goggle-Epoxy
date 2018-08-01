package io.github.plenglin.goggleapp.weather

import io.github.plenglin.goggle.util.activity.Activity
import io.github.plenglin.goggle.util.format
import java.awt.Graphics2D

class CurrentWeatherActivity : Activity() {

    private lateinit var g: Graphics2D

    override fun start() {
        g = ctx.display.createGraphics()
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

        val lat = ctx.hardware.gps.latitude
        val lon = ctx.hardware.gps.longitude
        WeatherResources.getCurrentWeatherData(lat, lon) {
            displayWeatherData(lat, lon, it!!)
        }
    }

    override fun stop() {
        g.dispose()
    }

    fun displayWeatherData(lat: Double, lon: Double, dat: OWMCurrentData) {
        g.clearRect(0, 0, ctx.display.displayWidth, ctx.display.displayHeight)
        g.font = ctx.resources.fontMedium
        g.drawString(dat.name, 1, 16)
        g.font = ctx.resources.fontSmall
        g.drawString("${dat.main.temp} C", 5, 24)
        g.drawString("${dat.main.pressure} hPa", 5, 32)
        g.drawString("${dat.main.humidity}% hum.", 5, 40)
        g.drawString(dat.weather.joinToString(", ") { it.main }, 64, 24)
        g.drawString("${lat.format(2)} ${if (lat > 0) "N" else "S"}, ${lon.format(2)} ${if (lon > 0) "E" else "W"}", 64, 32)
        if (dat.rain != null) {
            g.drawString("${dat.rain.last3}cm rain", 64, 40)
        } else if (dat.snow != null) {
            g.drawString("${dat.snow.last3}cm snow", 64, 40)
        }
    }

}