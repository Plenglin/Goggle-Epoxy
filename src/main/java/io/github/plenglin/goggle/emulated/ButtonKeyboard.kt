package io.github.plenglin.goggle.emulated

import io.github.plenglin.goggle.devices.input.Button
import org.slf4j.LoggerFactory
import java.awt.event.KeyEvent
import java.awt.event.KeyListener

class ButtonKeyboard(override val name: String, val key: Int) : Button, KeyListener {
    override var isPressed: Boolean = false
        private set

    private val log = LoggerFactory.getLogger(javaClass.name)

    override fun keyTyped(e: KeyEvent) {
    }

    override fun keyPressed(e: KeyEvent) {
        if (e.keyCode == key) {
            isPressed = true
            log.debug("{}: {}", key, isPressed)
        }
    }

    override fun keyReleased(e: KeyEvent) {
        if (e.keyCode == key) {
            isPressed = false
            log.debug("{}: {}", key, isPressed)
        }
    }
}