package io.github.plenglin.goggle.util

import java.util.*

class Scheduler {

    private val queue: Queue<Command> = LinkedList()

    fun addCommand(cmd: Command) {
        if (cmd.isRunning) {
            throw RuntimeException("Command has already been initialized!")
        }
        queue.offer(cmd)
    }

    fun update() {
        val c = queue.remove()
        if (!c.isRunning) {
            c.initialize()
        }
        c.update((System.currentTimeMillis() - c.lastExecuted).toInt())
        if (c.shouldTerminate()) {
            c.terminate()
        } else {
            queue.offer(c)
        }
    }

}