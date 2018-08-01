package io.github.plenglin.goggleapp.weather

data class OWMForecastData(val city: OWMCityInfo,
                           val cnt: Int,
                           val list: List<OWMDataPoint>) {
    init {
        list.forEach { it.city = this.city.name }
    }
}

data class OWMCityInfo(val name: String)

data class OWMDataPoint(val dt: Long,
                        val main: OWMMain,
                        val weather: List<OWMWeather>,
                        val wind: OWMWind,
                        val clouds: OWMClouds,
                        var city: String,
                        val rain: OWMPrecipitation,
                        val snow: OWMPrecipitation)

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

data class OWMPrecipitation(val `3h`: Double)
