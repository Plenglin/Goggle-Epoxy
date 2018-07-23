package io.github.plenglin.goggle.util

abstract class Command {

    var lastExecuted: Long = 0
    var isRunning: Boolean = false

    open fun initialize() {}

    open fun update(dt: Int) {}

    open fun shouldTerminate(): Boolean = false

    open fun terminate() {}

}
