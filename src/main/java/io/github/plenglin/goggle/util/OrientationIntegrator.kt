package io.github.plenglin.goggle.util

import io.github.plenglin.goggle.devices.motion.Accelerometer
import io.github.plenglin.goggle.devices.motion.Gyroscope
import io.github.plenglin.goggle.devices.motion.Magnetometer
import io.github.plenglin.goggle.util.scheduler.Command
import org.apache.commons.math3.geometry.euclidean.threed.Rotation
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D

/**
 * Coordinate system:
 *
 * - X/I: East
 * - Y/J: Down
 * - Z/K: North
 */
class OrientationIntegrator(val gyro: Gyroscope, val mag: Magnetometer, val acc: Accelerometer, private val compensation: Double = 0.02) : Command() {

    private val invCompensation = 1 - compensation
    private var state: Rotation = Rotation.IDENTITY

    override fun update(dt: Int) {
        val northRaw = mag.magneticField
        val down = acc.acceleration.normalize()
        val east = down.crossProduct(northRaw)
        val north = down.crossProduct(east).normalize()

        val delta = gyro.getDeltaRotation(dt)

        val absolute = Rotation(Vector3D.PLUS_K, Vector3D.PLUS_J, north, down)
        val relative = delta.applyTo(state)

        // Weighted average of the 2 rotations. Essentially, shitty slerp.
        val newAxis = relative.axis.scalarMultiply(invCompensation).add(absolute.axis.scalarMultiply(compensation))
        val newAngle = relative.angle * invCompensation + absolute.angle * invCompensation
        state = Rotation(newAxis, newAngle)
    }

}