package io.github.plenglin.goggle.embedded

import com.pi4j.io.gpio.GpioFactory
import com.pi4j.io.gpio.PinState
import com.pi4j.io.gpio.RaspiPin
import com.pi4j.io.spi.SpiChannel
import com.pi4j.io.spi.SpiFactory
import com.pi4j.io.spi.SpiMode
import io.github.plenglin.goggle.hardware.DisplaySSD1306SPI
import org.apache.log4j.BasicConfigurator
import java.awt.Color

fun main(args: Array<String>) {
    //val i2c = I2CFactory.getInstance(I2CBus.BUS_1)!!
    BasicConfigurator.configure()
    val gpio = GpioFactory.getInstance()
    val spi = SpiFactory.getInstance(SpiChannel.CS1, 1000000, SpiMode.MODE_0)
    val ds = DisplaySSD1306SPI(
            dev = spi,
            rst = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27, PinState.HIGH),
            dc = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_24, PinState.LOW)
    )
    ds.initialize()

    val g = ds.createGraphics()
    g.color = Color.WHITE
    //g.drawLine(0, 0, ds.displayWidth, ds.displayHeight)
    //g.fillRect(10, 10, 10, 10)
    //g.fillRect(30, 10, 10, 10)
    //g.fillRect(10, 30, 10, 10)
    //g.fillRect(30, 30, 10, 10)

    try {
        while (true) {
            for (y in 0 until ds.displayHeight) {
                for (x in 0 until ds.displayWidth) {
                    println("$x,$y")
                    g.fillRect(x, y, 1, 1)
                    ds.update(30)
                    Thread.sleep(25)
                }
            }
        }
    } finally {
        gpio.shutdown()
    }
}