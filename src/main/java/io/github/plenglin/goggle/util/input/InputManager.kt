package io.github.plenglin.goggle.util.input

import io.github.plenglin.goggle.util.scheduler.Command
import java.util.*

class InputManager : Command() {
    val queue: Queue<InputEvent> = LinkedList()
    var listener: (InputEvent) -> Unit = {}

    override fun update(dt: Int) {
        while (queue.isNotEmpty()) {
            listener(queue.remove())
        }
    }
}