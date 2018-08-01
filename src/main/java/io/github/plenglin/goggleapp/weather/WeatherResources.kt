package io.github.plenglin.goggleapp.weather

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.gson.responseObject
import org.slf4j.LoggerFactory

object WeatherResources {

    private val log = LoggerFactory.getLogger(javaClass)

    const val URL_FORECAST = "http://api.openweathermap.org/data/2.5/forecast"
    const val URL_WEATHER = "http://api.openweathermap.org/data/2.5/weather"

    lateinit var apiKey: String

    fun getCurrentWeatherData(lat: Double, lon: Double, cb: (OWMDataPoint?) -> Unit) {
        Fuel.get(URL_WEATHER, parameters = listOf(
                "appid" to apiKey,
                "lon" to lat,
                "lat" to lon,
                "units" to "metric"
        )).responseObject<OWMDataPoint> { request, _, (dat, err) ->
            log.debug("sent {}, received {}", request, dat)
            if (err != null) {
                log.error("{}", err)
            }
            cb(dat)
        }
    }

    fun getForecastData(lat: Double, lon: Double, cb: (OWMForecastData?) -> Unit) {
        Fuel.get(URL_WEATHER, parameters = listOf(
                "appid" to apiKey,
                "lon" to lat,
                "lat" to lon,
                "units" to "metric"
        )).responseObject<OWMForecastData> { request, _, (dat, err) ->
            log.debug("sent {}, received {}", request, dat)
            if (err != null) {
                log.error("{}", err)
            }
            cb(dat)
        }
    }

}
