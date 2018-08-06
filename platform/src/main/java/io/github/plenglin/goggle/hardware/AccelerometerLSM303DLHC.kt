package io.github.plenglin.goggle.hardware

import com.pi4j.io.i2c.I2CDevice
import io.github.plenglin.goggle.devices.motion.Accelerometer
import io.github.plenglin.goggle.util.fixSign
import io.github.plenglin.goggle.util.scheduler.Command
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import kotlin.experimental.and

class AccelerometerLSM303DLHC(val dev: I2CDevice, val scale: AccelerometerLSM303DLHCScale) : Accelerometer, Command() {
    override var acceleration: Vector3D = Vector3D.ZERO
        private set

    private val multiplier = scale.scl / 32768

    override fun initialize() {
        dev.write(0x20, 0x27)
        dev.write(0x23, scale.msg and 0x30)
    }

    override fun update(dt: Int) {
        acceleration = Vector3D(
                multiplier * ((dev.read(0x29) shl 8) or dev.read(0x28)).fixSign(32768).toDouble(),
                multiplier * ((dev.read(0x2b) shl 8) or dev.read(0x2a)).fixSign(32768).toDouble(),
                multiplier * ((dev.read(0x2d) shl 8) or dev.read(0x2c)).fixSign(32768).toDouble()
        )
    }

}

enum class AccelerometerLSM303DLHCScale(val msg: Byte, val scl: Double) {
    S_2(0x00, 2.0),
    S_4(0x10, 4.0),
    S_8(0x20, 8.0),
    S_16(0x30, 16.0)
}