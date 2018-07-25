package io.github.plenglin.goggle.commands

import io.github.plenglin.goggle.devices.input.Button
import io.github.plenglin.goggle.util.input.ButtonInputEvent
import io.github.plenglin.goggle.util.input.InputEvent
import io.github.plenglin.goggle.util.scheduler.Command
import java.util.*

class ButtonEventQueueFeeder(private val btn: Button, private val queue: Queue<InputEvent>) : Command() {
    private var prev: Boolean = false

    override fun initialize() {
        prev = btn.isPressed
    }

    override fun update(dt: Int) {
        val currentState = btn.isPressed
        if (currentState != prev) {
            queue.offer(ButtonInputEvent(btn.name, currentState))
            prev = currentState
        }
    }
}