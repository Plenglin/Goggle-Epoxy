package io.github.plenglin.goggleapp.weather

import io.github.plenglin.goggle.Context
import io.github.plenglin.goggle.util.activity.Activity
import io.github.plenglin.goggle.util.app.GoggleApp
import org.slf4j.LoggerFactory

class WeatherForecastApp : GoggleApp {
    override val appName: String = "weather-forecast"
    override val appLabel: String = "Weather Forecast"
    private val log = LoggerFactory.getLogger(javaClass)

    override fun onRegistered(ctx: Context) {
        WeatherResources.apiKey = ctx.env["OPEN_WEATHER_MAP_KEY"]!!
        log.info("Read API key: {}", WeatherResources.apiKey)
    }

    override fun createInitialActivity(): Activity = WeatherLoadingActivity()
}