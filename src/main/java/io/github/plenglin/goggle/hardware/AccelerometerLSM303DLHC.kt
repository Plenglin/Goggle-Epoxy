package io.github.plenglin.goggle.hardware

import com.pi4j.io.i2c.I2CDevice
import io.github.plenglin.goggle.devices.motion.Accelerometer
import io.github.plenglin.goggle.util.read
import io.github.plenglin.goggle.util.scheduler.Command
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import kotlin.experimental.and

class AccelerometerLSM303DLHC(val dev: I2CDevice, val addr: Int, val scale: AccelerometerLSM303DLHCScale) : Accelerometer, Command() {
    override var acceleration: Vector3D = Vector3D.ZERO

    private val multiplier = scale.scl / 32768

    override fun initialize() {
        // TODO: CHECK IF CORRECT
        dev.write(addr, byteArrayOf(0x20, 0x27))
        dev.write(addr, byteArrayOf(0x23, scale.msg.toByte() and 0x30))
    }

    override fun update(dt: Int) {
        acceleration = Vector3D(
                multiplier * ((dev.read(addr, 0x28) shl 8) or dev.read(addr, 0x29)).toDouble(),
                multiplier * ((dev.read(addr, 0x2a) shl 8) or dev.read(addr, 0x2b)).toDouble(),
                multiplier * ((dev.read(addr, 0x2c) shl 8) or dev.read(addr, 0x2d)).toDouble()
        )
    }

}

enum class AccelerometerLSM303DLHCScale(val msg: Short, val scl: Double) {
    S_2(0x10, 2.0),
    S_4(0x20, 4.0),
    S_8(0x30, 8.0)
}