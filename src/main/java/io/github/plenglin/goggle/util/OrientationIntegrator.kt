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
class OrientationIntegrator(private val gyro: Gyroscope,
                            private val mag: Magnetometer,
                            private val acc: Accelerometer,
                            private val compensation: Double = 0.02) : Command() {

    private val invCompensation = 1 - compensation

    var orientation: Rotation = Rotation.IDENTITY
        private set

    override fun update(dt: Int) {
        val northRaw = mag.magneticField
        val down = acc.acceleration
        val east = down.crossProduct(northRaw)
        val north = down.crossProduct(east)

        val delta = gyro.getDeltaRotation(dt)

        // Absolute orientation.
        val absolute = Rotation(Vector3D.PLUS_K, Vector3D.PLUS_J, north, down)

        // Dead reckoning orientation.
        val relative = delta.applyTo(orientation)

        // Weighted average of the 2 rotations. Essentially, shitty slerp.
        val newAxis = relative.axis.scalarMultiply(invCompensation).add(absolute.axis.scalarMultiply(compensation))  // avg. axes
        val newAngle = relative.angle * invCompensation + absolute.angle * compensation  // avg. angles
        orientation = Rotation(newAxis, newAngle)
    }

}