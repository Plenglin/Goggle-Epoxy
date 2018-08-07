package io.github.plenglin.goggle.embedded

import com.pi4j.io.gpio.GpioFactory
import com.pi4j.io.gpio.GpioPin
import com.pi4j.io.gpio.Pin
import com.pi4j.io.gpio.RaspiPin
import com.pi4j.io.i2c.I2CBus
import com.pi4j.io.i2c.I2CFactory
import com.pi4j.io.spi.SpiChannel
import com.pi4j.io.spi.SpiFactory
import com.pi4j.io.spi.SpiMode
import io.github.plenglin.goggle.hardware.DisplaySSD1306I2C
import io.github.plenglin.goggle.hardware.DisplaySSD1306SPI

fun main(args: Array<String>) {
    //val i2c = I2CFactory.getInstance(I2CBus.BUS_1)!!
    val gpio = GpioFactory.getInstance()
    val spi = SpiFactory.getInstance(SpiChannel.CS1, 8000000, SpiMode.MODE_0)
    val ds = DisplaySSD1306SPI(
            dev = spi,
            dc = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_24),
            rst = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27))
    ds.initialize()

    val g = ds.createGraphics()
    g.drawLine(0, 0, 128, 10)

    ds.update(10)
}