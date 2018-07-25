package io.github.plenglin.goggle

import com.pi4j.io.gpio.GpioFactory
import com.pi4j.io.i2c.I2CBus
import com.pi4j.io.i2c.I2CFactory
import io.github.plenglin.goggle.hardware.*

fun main(args: Array<String>) {
    val gpio = GpioFactory.getInstance()!!
    val i2c = I2CFactory.getInstance(I2CBus.BUS_1)!!

    val acc = AccelerometerLSM303DLHC(i2c.getDevice(0b0011001), AccelerometerLSM303DLHCScale.S_8)
    val mag = MagnetometerLSM303DLHC(i2c.getDevice(0b0011110), MagnetometerLSM303DLHCScale.S_81)
    val gyro = GyroL3GD20(i2c.getDevice(0x6B), GyroL3GD20Scale.S_2000)

    val mpl = WeatherMPL3115A2(i2c.getDevice(0x60))

    val ssd = DisplaySSD1306(i2c.getDevice(0x30))  // TODO FIND THE RIGHT ADDRESS

    val hw = Hardware(
            gpio = gpio, i2c = i2c,
            acc = acc, mag = mag, gyro = gyro,
            alt = mpl, bar = mpl, therm = mpl,
            display = ssd,
            commands = listOf(mpl, acc, mag, gyro)
    )

    Context(Resources(), hw).run()
}