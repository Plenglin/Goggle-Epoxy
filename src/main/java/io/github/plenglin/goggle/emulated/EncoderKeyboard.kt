package io.github.plenglin.goggle.emulated

import io.github.plenglin.goggle.devices.input.Encoder
import java.awt.event.KeyEvent
import java.awt.event.KeyListener

class EncoderKeyboard(override val name: String, val inc: Int, val dec: Int) : Encoder, KeyListener {
    override var ticks: Long = 0

    override fun keyTyped(e: KeyEvent?) {
    }

    override fun keyPressed(e: KeyEvent) {
        when (e.keyCode) {
            inc -> ticks++
            dec -> ticks--
        }
    }

    override fun keyReleased(e: KeyEvent?) {
    }

}