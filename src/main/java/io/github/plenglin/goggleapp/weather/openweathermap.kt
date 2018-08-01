package io.github.plenglin.goggleapp.weather

data class OWMWeatherData(val main: OWMMain,
                          val weather: List<OWMWeatherSub>,
                          val wind: OWMWind,
                          val clouds: OWMClouds,
                          val city: String)

data class OWMClouds(val all: Double)

data class OWMWeatherSub(val main: String,
                         val description: String,
                         val icon: String)

data class OWMWind(val speed: Double,
                   val deg: Double)

data class OWMMain(val temp: Double,
                   val pressure: Double,
                   val humidity: Double)