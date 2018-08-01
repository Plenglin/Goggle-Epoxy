package io.github.plenglin.goggleapp.weather

import io.github.plenglin.goggle.util.activity.Activity
import java.awt.Graphics2D

class CurrentWeatherActivity : Activity() {

    private lateinit var g: Graphics2D

    override fun start() {
        g = ctx.display.createGraphics()
    }

    override fun resume() {
        g.clearRect(0, 0, ctx.display.displayWidth, ctx.display.displayHeight)
        val lat = ctx.hardware.gps.latitude
        val lon = ctx.hardware.gps.longitude
        WeatherResources.getCurrentWeatherData(lat, lon) {
            it!!
            g.font = ctx.resources.fontMedium
            g.drawString(it.name, 1, 16)
            val cityNameWidth = g.fontMetrics.stringWidth(it.name)
            g.font = ctx.resources.fontSmall
            g.drawString("${it.main.temp} C", 5 + cityNameWidth, 8)
            g.drawString("${it.main.pressure} hPa", 5 + cityNameWidth, 16)
            g.drawString("${it.main.humidity}% hum.", 5 + cityNameWidth, 24)
        }
    }

    override fun stop() {
        g.dispose()
    }

}