package io.github.plenglin.goggle.emulated

import io.github.plenglin.goggle.devices.display.Display
import io.github.plenglin.goggle.util.scheduler.Command
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.swing.JPanel

class DisplaySwingEmulatedSSD1306 : JPanel(), Display {

    override val displayWidth: Int = 128
    override val displayHeight: Int = 64

    private val buf = BufferedImage(128, 64, BufferedImage.TYPE_BYTE_BINARY)

    override fun createGraphics(): Graphics2D = buf.createGraphics()

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g!!)
        g.drawImage(buf, 0, 0, SWING_WIDTH, SWING_HEIGHT, 0, 0, 128, 64, null)
    }

    override fun getPreferredSize(): Dimension {
        return Dimension(SWING_WIDTH, SWING_HEIGHT)
    }

    override fun getBackground(): Color {
        return Color.black
    }

    val updateCommand = object : Command() {
        override fun initialize() {
            this@DisplaySwingEmulatedSSD1306.repaint()
        }
        override fun update(dt: Int) {
            this@DisplaySwingEmulatedSSD1306.repaint()
        }
    }

    companion object {
        const val SWING_WIDTH = 512
        const val SWING_HEIGHT = 256
    }
}