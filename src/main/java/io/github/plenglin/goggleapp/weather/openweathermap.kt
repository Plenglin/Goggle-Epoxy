package io.github.plenglin.goggleapp.weather

import com.google.gson.annotations.SerializedName

data class OWMForecastData(val city: OWMCityInfo,
                           val cnt: Int,
                           val list: List<OWMForecastPoint>) {
    init {
        list.forEach { it.name = this.city.name }
    }
}

data class OWMCityInfo(val name: String)

data class OWMForecastPoint(val dt: Long,
                            val main: OWMMain,
                            val weather: List<OWMWeather>,
                            val wind: OWMWind,
                            val clouds: OWMClouds,
                            var name: String,
                            val rain: OWMPrecipitation,
                            val snow: OWMPrecipitation?)

data class OWMCurrentData(val dt: Long,
                          val main: OWMMain,
                          val weather: List<OWMWeather>,
                          val wind: OWMWind,
                          val clouds: OWMClouds,
                          var name: String,
                          val rain: OWMPrecipitation?,
                          val snow: OWMPrecipitation?)

data class OWMClouds(val all: Double)

data class OWMWeather(val main: String,
                      val description: String,
                      val icon: String)

data class OWMWind(val speed: Double,
                   val deg: Double)

data class OWMMain(val temp: Double,
                   val pressure: Double,
                   val humidity: Double,
                   val sea_level: Double,
                   val grnd_level: Double)

data class OWMPrecipitation(@SerializedName("3h") val last3: Double)
