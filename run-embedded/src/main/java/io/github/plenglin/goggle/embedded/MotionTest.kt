package io.github.plenglin.goggle.embedded

import com.pi4j.io.i2c.I2CBus
import com.pi4j.io.i2c.I2CFactory
import io.github.plenglin.goggle.hardware.*

fun main(args: Array<String>) {
    //val gpio = GpioFactory.getInstance()!!
    val i2c = I2CFactory.getInstance(I2CBus.BUS_1)!!
    val acc = AccelerometerLSM303DLHC(i2c.getDevice(0x19), AccelerometerLSM303DLHCScale.S_4)
    val mag = MagnetometerLSM303DLHC(i2c.getDevice(0x1e), MagnetometerLSM303DLHCScale.S_13)
    val gyro = GyroL3GD20(i2c.getDevice(0x69), GyroL3GD20Scale.S_2000)

    acc.initialize()
    mag.initialize()
    gyro.initialize()

    Thread.sleep(1000L)

    while (true) {
        //acc.update(20)
        mag.update(20)
        //gyro.update(20)
        Thread.sleep(20L)
        //println("a: ${acc.acceleration}")
        println("m: ${mag.magneticField}")
        //println("g: ${gyro.angularVelocity}")
        //println()
    }
}