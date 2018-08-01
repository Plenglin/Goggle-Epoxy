package io.github.plenglin.goggleapp.weather

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.gson.responseObject
import org.slf4j.LoggerFactory

object WeatherResources {

    private val log = LoggerFactory.getLogger(javaClass)

    const val URL_FORECAST = "http://api.openweathermap.org/data/2.5/forecast"
    const val URL_FORECAST_DAILY = "http://api.openweathermap.org/data/2.5/forecast/daily"
    const val URL_WEATHER = "http://api.openweathermap.org/data/2.5/weather"

    lateinit var apiKey: String

    fun getCurrentWeatherData(lat: Double, lon: Double, cb: (OWMCurrentData?) -> Unit) {
        Fuel.get(URL_WEATHER, parameters = listOf(
                "appid" to apiKey,
                "lon" to lon,
                "lat" to lat,
                "units" to "metric"
        )).responseObject<OWMCurrentData> { _, _, (dat, err) ->
            log.debug("received {}", dat)
            if (err != null) {
                log.error("Error while fetching data!", err.exception)
            }
            cb(dat)
        }
    }

    fun getForecastData(lat: Double, lon: Double, cb: (OWMForecastData?) -> Unit) {
        Fuel.get(URL_FORECAST, parameters = listOf(
                "appid" to apiKey,
                "lon" to lon,
                "lat" to lat,
                "units" to "metric"
        )).responseObject<OWMForecastData> { _, _, (dat, err) ->
            log.debug("received {}", dat)
            if (err != null) {
                log.error("Error while fetching data!", err.exception)
            }
            cb(dat)
        }
    }

    fun getDailyForecastData(lat: Double, lon: Double, cb: (OWMForecastData?) -> Unit) {
        Fuel.get(URL_FORECAST_DAILY, parameters = listOf(
                "appid" to apiKey,
                "lon" to lon,
                "lat" to lat,
                "units" to "metric"
        )).responseObject<OWMForecastData> { _, _, (dat, err) ->
            log.debug("received {}", dat)
            if (err != null) {
                log.error("Error while fetching data!", err.exception)
            }
            cb(dat)
        }
    }

}
