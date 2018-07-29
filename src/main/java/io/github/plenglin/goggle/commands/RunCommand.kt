package io.github.plenglin.goggle.commands

import io.github.plenglin.goggle.util.scheduler.Command

class RunCommand(val run: () -> Unit) : Command() {

    constructor(run: Runnable) : this(run::run)

    override fun initialize() {
        run()
    }

    override fun shouldTerminate(): Boolean = true

}