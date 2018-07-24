package io.github.plenglin.goggle.hardware

import com.pi4j.io.i2c.I2CDevice
import io.github.plenglin.goggle.devices.display.Display
import io.github.plenglin.goggle.util.scheduler.Command
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte

class DisplaySSD1306(val dev: I2CDevice) : Command(), Display {

    private val image = BufferedImage(128, 64, BufferedImage.TYPE_BYTE_BINARY)

    override fun createGraphics(): Graphics2D = image.createGraphics()

    override fun initialize() {
        //dev.write()
    }

    override fun update(dt: Int) {
        val data = (image.raster.dataBuffer as DataBufferByte).data
        dev.write(0x21, byteArrayOf(0, 127))  // Horizontal limits
        dev.write(0x22, byteArrayOf(0, 7))  // Vertical limits
        for (i in data.indices step 16) {
            dev.write(0x40, data, 0, 16)
        }
    }

}