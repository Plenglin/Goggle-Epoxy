package io.github.plenglin.goggleapp.weather

import java.time.LocalDate

data class DayWeatherData(val day: LocalDate,
                          val lowTemp: Double,
                          val highTemp: Double,
                          val pressure: Double,
                          val conditions: List<String>)