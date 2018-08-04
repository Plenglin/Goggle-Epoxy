package io.github.plenglin.goggle.emulated

import io.github.plenglin.goggle.devices.input.Encoder
import org.slf4j.LoggerFactory
import java.awt.event.KeyEvent
import java.awt.event.KeyListener

class EncoderKeyboard(override val name: String, val inc: Int, val dec: Int) : Encoder, KeyListener {
    override var ticks: Long = 0

    private val log = LoggerFactory.getLogger(javaClass)

    override fun keyTyped(e: KeyEvent?) {
    }

    override fun keyPressed(e: KeyEvent) {
        when (e.keyCode) {
            inc -> {
                log.debug("{}: increment", name)
                ticks++
            }
            dec -> {
                log.debug("{}: decrement", name)
                ticks--
            }
        }
    }

    override fun keyReleased(e: KeyEvent?) {
    }

}