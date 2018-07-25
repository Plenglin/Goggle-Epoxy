package io.github.plenglin.goggle.util.scheduler

import java.util.*
import java.util.logging.Logger

class Scheduler {

    private val log = Logger.getLogger(javaClass.name)

    private val queue: Queue<Command> = LinkedList()

    fun addCommand(cmd: Command) {
        if (cmd.isRunning) {
            throw RuntimeException("Command has already been initialized!")
        }
        queue.offer(cmd)
    }

    fun update() {
        val c = queue.remove()
        log.fine { "Processing $c" }
        if (!c.isRunning) {
            log.info { "Initializing $c" }
            c.initialize()
            c.isRunning = true
        }
        log.fine { "Updating $c" }
        val currentTime = System.currentTimeMillis()
        c.update((currentTime - c.lastExecuted).toInt())
        c.lastExecuted = currentTime
        if (c.shouldTerminate()) {
            log.info { "Terminating $c" }
            c.terminate()
        } else {
            queue.offer(c)
        }
    }

}