package io.github.plenglin.goggle.commands

import io.github.plenglin.goggle.util.scheduler.Command
import io.github.plenglin.goggle.util.scheduler.Scheduler

class PeriodicCommand(val scheduler: Scheduler, val command: Command, val period: Long, val initialDelay: Long = 0) : Command() {

    private var nextExecution = 0L
    private var stopFlag = false

    override fun initialize() {
        nextExecution = System.currentTimeMillis() + initialDelay
    }

    override fun update(dt: Int) {
        if (System.currentTimeMillis() >= nextExecution) {
            nextExecution += period
            scheduler.addCommand(command)
        }
    }

    override fun shouldTerminate(): Boolean = stopFlag

    fun stopExecution() {
        stopFlag = true
    }

}