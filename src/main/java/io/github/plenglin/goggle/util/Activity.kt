package io.github.plenglin.goggle.util

interface Activity {
    fun start() {}

    fun resume() {}

    fun update(dt: Int) {}

    fun suspend() {}

    fun stop() {}
}