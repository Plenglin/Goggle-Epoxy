package io.github.plenglin.goggle.commands

import io.github.plenglin.goggle.util.scheduler.Command
import io.github.plenglin.goggle.util.scheduler.Scheduler

class WaitCommand(val scheduler: Scheduler, val command: Command, val terminationCondition: () -> Boolean) : Command() {

    override fun shouldTerminate(): Boolean {
        return terminationCondition()
    }

    override fun terminate() {
        scheduler.addCommand(command)
    }

}