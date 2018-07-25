package io.github.plenglin.goggle.commands

import io.github.plenglin.goggle.devices.input.Encoder
import io.github.plenglin.goggle.util.input.EncoderInputEvent
import io.github.plenglin.goggle.util.input.InputEvent
import io.github.plenglin.goggle.util.scheduler.Command
import java.util.*

class EncoderEventQueueFeeder(private val enc: Encoder, private val queue: Queue<InputEvent>) : Command() {
    private var last: Long = 0

    override fun update(dt: Int) {
        val currentTicks = enc.ticks
        val delta: Int = (currentTicks - last).toInt()
        if (delta != 0) {
            queue.offer(EncoderInputEvent(enc.name, delta))
            last = currentTicks
        }
    }
}