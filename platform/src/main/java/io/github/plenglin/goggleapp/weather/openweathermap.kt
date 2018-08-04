package io.github.plenglin.goggleapp.weather

import com.google.gson.annotations.SerializedName
import io.github.plenglin.goggle.SECONDS_PER_DAY
import java.time.LocalDate

data class OWMCurrentData(val dt: Long,
                          val main: OWMMain,
                          val weather: List<OWMWeather>,
                          val wind: OWMWind,
                          val clouds: OWMClouds,
                          var name: String,
                          val rain: OWMPrecipitation?,
                          val snow: OWMPrecipitation?)

data class OWMForecastData(val city: OWMCityInfo,
                           val cnt: Int,
                           val list: List<OWMForecastPoint>) {

    val days get() =
        list.groupBy { LocalDate.ofEpochDay(it.dt / SECONDS_PER_DAY ) }  // Group by days
                .map { (day, pts) ->
                    println("$day: $pts")
                    val temps = pts.map { it.main.temp }
                    DayWeatherData(
                            day = day,
                            highTemp = temps.max()!!,
                            lowTemp = temps.min()!!,
                            pressure = pts.sumByDouble { it.main.pressure },
                            conditions = pts.map { it.weather.map(OWMWeather::main) }.flatten().distinct()
                    )
                }.sortedBy { it.day }

}

data class OWMDailyForecastData(val city: OWMCityInfo,
                           val cnt: Int,
                           val list: List<OWMForecastDay>)

data class OWMCityInfo(val id: Int, val name: String)

data class OWMForecastPoint(val dt: Long,
                            val main: OWMMain,
                            val weather: List<OWMWeather>,
                            val wind: OWMWind,
                            val clouds: OWMClouds,
                            val rain: OWMPrecipitation?,
                            val snow: OWMPrecipitation?)

data class OWMForecastDay(val dt: Long,
                          val temp: OWMDayTempData,
                          val pressure: Double,
                          val humidity: Double,
                          val weather: List<OWMWeather>)

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

data class OWMDayTempData(val min: Double,
                          val max: Double)
