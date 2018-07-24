package io.github.plenglin.goggle

import com.pi4j.io.gpio.GpioFactory
import com.pi4j.io.i2c.I2CBus
import com.pi4j.io.i2c.I2CFactory
import io.github.plenglin.goggle.hardware.AccelerometerLSM303DLHC
import io.github.plenglin.goggle.hardware.AccelerometerLSM303DLHCScale

fun main(args: Array<String>) {
    val gpio = GpioFactory.getInstance()
    val i2c = I2CFactory.getInstance(I2CBus.BUS_1)
    val hw = Hardware(
            gpio = gpio,
            acc = AccelerometerLSM303DLHC(i2c.getDevice(0b0011001), AccelerometerLSM303DLHCScale.S_8)
    )
}