package io.github.plenglin.goggleapp.weather

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.gson.responseObject

object WeatherResources {

    const val URL_FORECAST = "http://api.openweathermap.org/data/2.5/forecast"
    const val URL_WEATHER = "http://api.openweathermap.org/data/2.5/weather"

    lateinit var apiKey: String

    fun getCurrentWeatherData(lat: Double, lon: Double, cb: (OWMWeatherData?) -> Unit) {
        Fuel.get(URL_WEATHER, parameters = listOf(
                "appid" to apiKey,
                "lon" to lat,
                "lat" to lon,
                "units" to "metric"
        )).responseObject<OWMWeatherData> { _, _, (dat, _) ->
            cb(dat)
        }
    }

}
