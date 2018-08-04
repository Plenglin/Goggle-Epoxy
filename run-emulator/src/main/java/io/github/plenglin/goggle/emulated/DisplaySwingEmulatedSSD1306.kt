package io.github.plenglin.goggle.emulated

import io.github.plenglin.goggle.devices.display.Display
import io.github.plenglin.goggle.util.scheduler.Command
import java.awt.*
import java.awt.event.WindowEvent
import java.awt.image.BufferedImage
import javax.swing.JFrame
import javax.swing.JPanel

class DisplaySwingEmulatedSSD1306(val closeJFrame: JFrame? = null) : JPanel(), Display {

    override val displayWidth: Int = 128
    override val displayHeight: Int = 64

    private var cleared = false
    private val buf = BufferedImage(128, 64, BufferedImage.TYPE_BYTE_BINARY)

    override fun createGraphics(): Graphics2D = buf.createGraphics()

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g!!)
        if (!cleared) {
            g.color = Color.gray
            g.fillRect(0, 0, width, height)
            cleared = true
        }
        g.drawImage(buf, 0, 0, SWING_WIDTH, SWING_HEIGHT, 0, 0, 128, 64, null)
    }

    override fun getPreferredSize(): Dimension {
        return Dimension(SWING_WIDTH, SWING_HEIGHT)
    }

    override fun getBackground(): Color {
        return Color.black
    }

    val updateCommand = object : Command() {
        override fun update(dt: Int) {
            paintImmediately(Rectangle(0, 0, SWING_WIDTH, SWING_HEIGHT))
        }

        override fun terminate() {
            if (closeJFrame != null) {
                dispatchEvent(WindowEvent(closeJFrame, WindowEvent.WINDOW_CLOSING))
            }
        }
    }

    companion object {
        /*const val SWING_WIDTH = 128
        const val SWING_HEIGHT = 64*/

        /*const val SWING_WIDTH = 256
        const val SWING_HEIGHT = 128*/

        const val SWING_WIDTH = 512
        const val SWING_HEIGHT = 256
    }
}