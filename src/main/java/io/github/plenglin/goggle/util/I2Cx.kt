package io.github.plenglin.goggle.util

import com.pi4j.io.i2c.I2CDevice

fun I2CDevice.read(addr: Int, reg: Byte): Int {
    write(addr, reg)
    return read(addr)
}