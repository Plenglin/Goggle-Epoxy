package io.github.plenglin.goggle.embedded

import com.pi4j.io.gpio.GpioFactory
import com.pi4j.io.gpio.PinPullResistance
import com.pi4j.io.gpio.RaspiPin
import com.pi4j.io.i2c.I2CBus
import com.pi4j.io.i2c.I2CFactory
import io.github.plenglin.goggle.Context
import io.github.plenglin.goggle.Hardware
import io.github.plenglin.goggle.Resources
import io.github.plenglin.goggle.hardware.*
import io.github.plenglin.goggle.util.app.QualifiedAppDef
import org.apache.log4j.BasicConfigurator

fun main(args: Array<String>) {
    BasicConfigurator.configure()
    val gpio = GpioFactory.getInstance()!!
    val i2c = I2CFactory.getInstance(I2CBus.BUS_1)!!

    val acc = AccelerometerLSM303DLHC(i2c.getDevice(0b0011001), AccelerometerLSM303DLHCScale.S_8)
    val mag = MagnetometerLSM303DLHC(i2c.getDevice(0b0011110), MagnetometerLSM303DLHCScale.S_81)
    val gyro = GyroL3GD20(i2c.getDevice(0x6B), GyroL3GD20Scale.S_2000)

    val mpl = WeatherMPL3115A2(i2c.getDevice(0x60))

    val ssd = DisplaySSD1306(i2c.getDevice(0x7a))  // potentially 0x3c

    val btnX = ButtonGPIO("x", gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_UP))
    val btnY = ButtonGPIO("y", gpio.provisionDigitalInputPin(RaspiPin.GPIO_03, PinPullResistance.PULL_UP))
    val btnZ = ButtonGPIO("z", gpio.provisionDigitalInputPin(RaspiPin.GPIO_06, PinPullResistance.PULL_UP))
    val btnS = ButtonGPIO("s", gpio.provisionDigitalInputPin(RaspiPin.GPIO_22, PinPullResistance.PULL_UP))
    val btnH = ButtonGPIO("h", gpio.provisionDigitalInputPin(RaspiPin.GPIO_25, PinPullResistance.PULL_UP))

    val encSel = EncoderGPIO("sel",
            gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.PULL_UP),
            gpio.provisionDigitalInputPin(RaspiPin.GPIO_05, PinPullResistance.PULL_UP)
    )

    val hw = Hardware(
            gpio = gpio, i2c = i2c,
            acc = acc, mag = mag, gyro = gyro,
            alt = mpl, bar = mpl, therm = mpl,
            display = ssd,
            buttons = listOf(btnX, btnY, btnZ, btnS, btnH),
            encoders = listOf(encSel),
            commands = listOf(mpl, acc, mag, gyro)
    )

    Context(Resources(), hw,
            apps = listOf(
                    QualifiedAppDef("io.github.plenglin.goggleapp.astronomy.AstronomyApp"),
                    QualifiedAppDef("io.github.plenglin.goggleapp.tetris.TetrisApp"),
                    QualifiedAppDef("io.github.plenglin.goggleapp.weather.WeatherForecastApp")
            )
    ).run()
}