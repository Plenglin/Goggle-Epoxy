package io.github.plenglin.goggle.commands

import io.github.plenglin.goggle.util.scheduler.Command
import io.github.plenglin.goggle.util.scheduler.Scheduler

class SeriesCommand(val scheduler: Scheduler, vararg val commands: Command) : Command() {
    private var i = 0
    private var shouldTerminate = false

    override fun initialize() {
        scheduler.addCommand(commands[i])
    }

    override fun update(dt: Int) {
        if (!commands[i].isRunning) {
            i++
            if (i == commands.size) {
                shouldTerminate = true
            } else {
                scheduler.addCommand(commands[i])
            }
        }
    }

}