package io.github.plenglin.goggle.util.scheduler

import org.slf4j.LoggerFactory

class Scheduler {

    private val log = LoggerFactory.getLogger(javaClass.name)

    private val commands: MutableList<Command> = mutableListOf()
    private val commandBuffer: MutableList<Command> = mutableListOf()

    fun addCommand(cmd: Command) {
        if (cmd.isRunning) {
            throw RuntimeException("Command has already been initialized!")
        }
        commandBuffer.add(cmd)
    }

    fun update() {
        commands.addAll(commandBuffer)
        commandBuffer.clear()
        commands.forEach { c ->
            log.trace("Processing {}", c)
            if (!c.isRunning) {
                log.info("Initializing {}", c)
                c.initialize()
                c.isRunning = true
            }
            log.trace("Updating {}", c)
            val currentTime = System.currentTimeMillis()
            c.update((currentTime - c.lastExecuted).toInt())
            c.lastExecuted = currentTime
            if (c.shouldTerminate()) {
                log.info("Terminating {}", c)
                c.terminate()
                c.isRunning = false
            }
        }
        commands.removeAll { !it.isRunning }
    }

}