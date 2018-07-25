package io.github.plenglin.goggle.emulated

import io.github.plenglin.goggle.devices.display.Display
import io.github.plenglin.goggle.util.scheduler.Command
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.swing.JPanel

class DisplaySwingWindow : JPanel(), Display {
    private val buf = BufferedImage(128, 64, BufferedImage.TYPE_BYTE_BINARY)

    override fun createGraphics(): Graphics2D = buf.createGraphics()

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g!!)
        g.drawImage(buf, 0, 0, WIDTH, HEIGHT, 0, 0, 128, 64, null)
    }

    override fun getPreferredSize(): Dimension {
        return Dimension(WIDTH, HEIGHT)
    }

    override fun getBackground(): Color {
        return Color.black
    }

    val updateCommand = object : Command() {
        override fun initialize() {
            this@DisplaySwingWindow.repaint()
        }
        override fun update(dt: Int) {
            this@DisplaySwingWindow.repaint()
        }
    }

    companion object {
        val WIDTH = 512
        val HEIGHT = 256
    }
}