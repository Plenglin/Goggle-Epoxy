package io.github.plenglin.goggleapp.weather

import java.time.LocalDate

data class DayWeatherData(val day: LocalDate,
                          val lowTemp: Double,
                          val highTemp: Double,
                          val pressure: Double,
                          val conditions: List<String>)

data class WeatherContext(val current: OWMCurrentData, val forecast: OWMForecastData, val lat: Double, val lon: Double)
