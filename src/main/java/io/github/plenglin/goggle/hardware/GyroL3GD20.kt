package io.github.plenglin.goggle.hardware

import com.pi4j.io.i2c.I2CDevice
import io.github.plenglin.goggle.devices.motion.Gyroscope
import io.github.plenglin.goggle.util.scheduler.Command
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D


class GyroL3GD20(val dev: I2CDevice, scale: GyroL3GD20Scale) : Gyroscope, Command() {
    override var angularVelocity: Vector3D = Vector3D.ZERO
        private set

    private val sclMsg = scale.code
    private val multiplier = scale.scl * Math.PI / 180 / 32768

    override fun initialize() {
        dev.write(0x20, sclMsg.toByte())
    }

    override fun update(dt: Int) {
        angularVelocity = Vector3D(
                ((dev.read(0x28) shl 8) or dev.read(0x29)) * multiplier,
                ((dev.read(0x2a) shl 8) or dev.read(0x2b)) * multiplier,
                ((dev.read(0x2c) shl 8) or dev.read(0x2d)) * multiplier
        )
    }
}

enum class GyroL3GD20Scale(val code: Short, val scl: Double) {
    S_250(0x00, 250.0),
    S_500(0x10, 500.0),
    S_2000(0x20, 2000.0)
}
