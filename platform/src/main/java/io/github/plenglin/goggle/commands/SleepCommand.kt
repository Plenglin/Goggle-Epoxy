package io.github.plenglin.goggle.commands

import io.github.plenglin.goggle.util.scheduler.Command

class SleepCommand(val delta: Long) : Command() {

    private var end = 0L

    override fun initialize() {
        end = System.currentTimeMillis() + delta
    }

    override fun shouldTerminate(): Boolean {
        return System.currentTimeMillis() >= end
    }

}