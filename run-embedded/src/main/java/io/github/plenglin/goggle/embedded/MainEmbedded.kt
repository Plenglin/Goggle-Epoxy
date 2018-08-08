package io.github.plenglin.goggle.embedded

import com.pi4j.io.gpio.GpioFactory
import com.pi4j.io.gpio.PinPullResistance
import com.pi4j.io.gpio.PinState
import com.pi4j.io.gpio.RaspiPin
import com.pi4j.io.i2c.I2CBus
import com.pi4j.io.i2c.I2CFactory
import com.pi4j.io.spi.SpiChannel
import com.pi4j.io.spi.SpiFactory
import com.pi4j.io.spi.SpiMode
import io.github.plenglin.goggle.Context
import io.github.plenglin.goggle.Hardware
import io.github.plenglin.goggle.Resources
import io.github.plenglin.goggle.hardware.*
import io.github.plenglin.goggle.util.app.QualifiedAppDef
import io.github.plenglin.goggle.util.input.rescale
import org.apache.log4j.BasicConfigurator

fun main(args: Array<String>) {
    BasicConfigurator.configure()
    val gpio = GpioFactory.getInstance()!!
    val i2c = I2CFactory.getInstance(I2CBus.BUS_1)!!

    val acc = AccelerometerLSM303DLHC(i2c.getDevice(0b0011001), AccelerometerLSM303DLHCScale.S_4)
    val mag = MagnetometerLSM303DLHC(i2c.getDevice(0b0011110), MagnetometerLSM303DLHCScale.S_13)
    val gyro = GyroL3GD20(i2c.getDevice(0x69), GyroL3GD20Scale.S_2000)

    val mpl = WeatherMPL3115A2(i2c.getDevice(0x60))

    val ssd = DisplaySSD1306SPI(
            dev = SpiFactory.getInstance(SpiChannel.CS1, 1000000, SpiMode.MODE_0),
            rst = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27, PinState.HIGH),
            dc = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_24, PinState.LOW)
    )

    val btnX = ButtonGPIO("x", gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_UP), false)
    val btnY = ButtonGPIO("y", gpio.provisionDigitalInputPin(RaspiPin.GPIO_03, PinPullResistance.PULL_UP), false)
    val btnZ = ButtonGPIO("z", gpio.provisionDigitalInputPin(RaspiPin.GPIO_06, PinPullResistance.PULL_UP), false)
    val btnS = ButtonGPIO("s", gpio.provisionDigitalInputPin(RaspiPin.GPIO_22, PinPullResistance.PULL_UP), false)
    val btnH = ButtonGPIO("h", gpio.provisionDigitalInputPin(RaspiPin.GPIO_28, PinPullResistance.PULL_UP), false)

    val encSel = EncoderGPIO("sel",
            gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.PULL_UP),
            gpio.provisionDigitalInputPin(RaspiPin.GPIO_04, PinPullResistance.PULL_UP)
    ).rescale(4)

    val hw = Hardware(
            gpio = gpio, i2c = i2c,
            acc = acc, mag = mag, gyro = gyro,
            alt = mpl, bar = mpl, therm = mpl,
            display = ssd,
            buttons = listOf(btnX, btnY, btnZ, btnS, btnH),
            encoders = listOf(encSel),
            commands = listOf(mpl, acc, mag, gyro, ssd)
    )

    Context(Resources(), hw,
            apps = listOf(
                    QualifiedAppDef("io.github.plenglin.goggleapp.astronomy.AstronomyApp"),
                    QualifiedAppDef("io.github.plenglin.goggleapp.tetris.TetrisApp"),
                    QualifiedAppDef("io.github.plenglin.goggleapp.weather.WeatherForecastApp")
            )
    ).run()
}