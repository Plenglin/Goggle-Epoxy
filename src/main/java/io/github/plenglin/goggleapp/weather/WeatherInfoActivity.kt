package io.github.plenglin.goggleapp.weather

import io.github.plenglin.goggle.util.activity.Activity

class WeatherInfoActivity : Activity() {

    override fun resume() {
        WeatherResources.apiKey
    }

}