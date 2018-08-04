package io.github.plenglin.goggle.hardware

import com.pi4j.io.i2c.I2CDevice
import io.github.plenglin.goggle.devices.motion.Magnetometer
import io.github.plenglin.goggle.util.scheduler.Command
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import kotlin.experimental.and

class MagnetometerLSM303DLHC(val dev: I2CDevice, val scale: MagnetometerLSM303DLHCScale) : Magnetometer, Command() {
    override var magneticField: Vector3D = Vector3D.ZERO
        private set

    private val multiplier: Double = scale.scl / 2048

    override fun initialize() {
        // TODO: CHECK IF CORRECT
        dev.write(0x20, 0x27)
        dev.write(0x23, scale.msg.toByte() and 0x30)
    }

    override fun update(dt: Int) {
        magneticField = Vector3D(
                multiplier * ((dev.read(0x03) shl 8) or dev.read(0x04)).toDouble(),
                multiplier * ((dev.read(0x05) shl 8) or dev.read(0x06)).toDouble(),
                multiplier * ((dev.read(0x07) shl 8) or dev.read(0x08)).toDouble()
        )
    }

}

enum class MagnetometerLSM303DLHCScale(val msg: Short, val scl: Double) {
    S_13(0x20, 1.3),
    S_25(0x60, 1.3),
    S_40(0x80, 1.3),
    S_81(0xD0, 1.3),
}