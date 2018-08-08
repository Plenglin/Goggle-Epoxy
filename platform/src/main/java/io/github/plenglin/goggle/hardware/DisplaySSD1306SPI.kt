package io.github.plenglin.goggle.hardware

import com.pi4j.io.gpio.GpioPinDigitalOutput
import com.pi4j.io.spi.SpiDevice
import io.github.plenglin.goggle.devices.display.Display
import io.github.plenglin.goggle.util.scheduler.Command
import org.slf4j.LoggerFactory
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import kotlin.experimental.and
import kotlin.experimental.or

/**
 * Copied, pasted, and ported from https://github.com/adafruit/Adafruit_SSD1306/
 */
class DisplaySSD1306SPI(val dev: SpiDevice, val dc: GpioPinDigitalOutput, val rst: GpioPinDigitalOutput) : Command(), Display {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)

        const val SSD1306_SETCONTRAST = 0x81
        const val SSD1306_DISPLAYALLON_RESUME = 0xA4
        const val SSD1306_DISPLAYALLON = 0xA5
        const val SSD1306_NORMALDISPLAY = 0xA6
        const val SSD1306_INVERTDISPLAY = 0xA7
        const val SSD1306_DISPLAYOFF = 0xAE
        const val SSD1306_DISPLAYON = 0xAF

        const val SSD1306_SETDISPLAYOFFSET = 0xD3
        const val SSD1306_SETCOMPINS = 0xDA

        const val SSD1306_SETVCOMDETECT = 0xDB

        const val SSD1306_SETDISPLAYCLOCKDIV = 0xD5
        const val SSD1306_SETPRECHARGE = 0xD9

        const val SSD1306_SETMULTIPLEX = 0xA8

        const val SSD1306_SETLOWCOLUMN = 0x00
        const val SSD1306_SETHIGHCOLUMN = 0x10

        const val SSD1306_SETSTARTLINE = 0x40

        const val SSD1306_MEMORYMODE = 0x20
        const val SSD1306_COLUMNADDR = 0x21
        const val SSD1306_PAGEADDR   = 0x22

        const val SSD1306_COMSCANINC = 0xC0
        const val SSD1306_COMSCANDEC = 0xC8

        const val SSD1306_SEGREMAP = 0xA0

        const val SSD1306_CHARGEPUMP = 0x8D

        const val SSD1306_EXTERNALVCC = 0x1
        const val SSD1306_SWITCHCAPVCC = 0x2

        const val SSD1306_ACTIVATE_SCROLL = 0x2F
        const val SSD1306_DEACTIVATE_SCROLL = 0x2E
        const val SSD1306_SET_VERTICAL_SCROLL_AREA = 0xA3
        const val SSD1306_RIGHT_HORIZONTAL_SCROLL = 0x26
        const val SSD1306_LEFT_HORIZONTAL_SCROLL = 0x27
        const val SSD1306_VERTICAL_AND_RIGHT_HORIZONTAL_SCROLL = 0x29
        const val SSD1306_VERTICAL_AND_LEFT_HORIZONTAL_SCROLL = 0x2A
    }

    override val displayWidth: Int = 128
    override val displayHeight: Int = 64

    private val image = BufferedImage(displayWidth, displayHeight, BufferedImage.TYPE_BYTE_BINARY)

    override fun createGraphics(): Graphics2D = image.createGraphics()

    private fun ssd1306_command(cmd: Int) {
        ssd1306_command(cmd.toByte())
    }

    private fun ssd1306_command(cmd: Byte) {
        log.trace("Sending CMD: 0x{}", Integer.toHexString(cmd.toInt()))
        dev.write(cmd)
    }

    override fun initialize() {
        // Reset the display
        rst.high()
        Thread.sleep(1)
        rst.low()
        Thread.sleep(10)
        rst.high()

        dc.low()

        ssd1306_command(SSD1306_DISPLAYOFF)                    // 0xAE
        ssd1306_command(SSD1306_SETDISPLAYCLOCKDIV)            // 0xD5
        ssd1306_command(0x80.toByte())                           // the suggested ratio 0x80
        ssd1306_command(SSD1306_SETMULTIPLEX)                  // 0xA8
        ssd1306_command(0x3F.toByte())
        ssd1306_command(SSD1306_SETDISPLAYOFFSET)              // 0xD3
        ssd1306_command(0x0.toByte())                            // no offset
        ssd1306_command(SSD1306_SETSTARTLINE or 0x0)   // line #0
        ssd1306_command(SSD1306_CHARGEPUMP)                    // 0x8D
        ssd1306_command(0x14.toByte())
        ssd1306_command(SSD1306_MEMORYMODE)                    // 0x20

        ssd1306_command(0x00.toByte())                           // 0x0 act like ks0108
        //ssd1306_command(0x01.toByte())

        ssd1306_command(SSD1306_SEGREMAP or 0x1)
        ssd1306_command(SSD1306_COMSCANDEC)
        ssd1306_command(SSD1306_SETCOMPINS)                    // 0xDA
        ssd1306_command(0x12.toByte())
        ssd1306_command(SSD1306_SETCONTRAST)                   // 0x81
        ssd1306_command(0xCF.toByte())
        ssd1306_command(SSD1306_SETPRECHARGE)                  // 0xd9
        ssd1306_command(0xF1.toByte())
        ssd1306_command(SSD1306_SETVCOMDETECT)                 // 0xDB
        ssd1306_command(0x40.toByte())
        ssd1306_command(SSD1306_DISPLAYALLON_RESUME)           // 0xA4
        ssd1306_command(SSD1306_NORMALDISPLAY)

        ssd1306_command(SSD1306_DISPLAYON)//--turn on oled panel

    }

    override fun update(dt: Int) {
        val start = System.currentTimeMillis()

        val data = (image.raster.dataBuffer as DataBufferByte).data
        val data2 = ByteArray(data.size)

        for (j in 0 until displayHeight step 8) {  // Iterate through 8x8 bit grids
            for (i in 0 until displayWidth step 8) {

                for (x in 0 until 8) {
                    var out: Byte = 0
                    val mask: Byte = (0x80 ushr x).toByte()

                    for (y in 0 until 8) {
                        //println("jy: ${(j + y) * displayWidth}; i8: ${i / 8}")
                        val si = (j + y) * displayWidth / 8 + i / 8
                        val state = data[si] and mask
                        if (state != 0.toByte()) {
                            out = out or (0x1 shl y).toByte()
                        }
                    }

                    val di = j * displayWidth / 8 + (i + x)
                    data2[di] = out
                }

            }
        }

        ssd1306_command(SSD1306_COLUMNADDR)
        ssd1306_command(0)
        ssd1306_command(displayWidth - 1)

        ssd1306_command(SSD1306_PAGEADDR)
        ssd1306_command(0)
        ssd1306_command(7) // Page end address

        dc.high()
        dev.write(data2, 0, data2.size)

        log.debug("Wrote {} bytes in {} ms", data2.size, System.currentTimeMillis() - start)
    }

}
