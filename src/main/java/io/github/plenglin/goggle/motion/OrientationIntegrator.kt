package io.github.plenglin.goggle.motion

import org.apache.commons.math3.complex.Quaternion
import org.apache.commons.math3.geometry.euclidean.threed.Rotation
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D

/**
 * Coordinate system:
 *
 * - X/I: East
 * - Y/J: Down
 * - Z/K: North
 */
class OrientationIntegrator(val gyro: Gyroscope, val mag: Magnetometer, val acc: Accelerometer, private val compensation: Double = 0.02) {

    private val invCompensation = 1 - compensation
    private var state: Rotation = Rotation.IDENTITY

    fun update(dt: Int) {
        val northRaw = mag.getMagneticField()
        val down = acc.getAcceleration().normalize()
        val east = down.crossProduct(northRaw)
        val north = down.crossProduct(east).normalize()

        val delta = gyro.getDeltaRotation(dt)

        val absolute = Rotation(Vector3D.PLUS_K, Vector3D.PLUS_J, north, down)
        val relative = delta.applyTo(state)

        // Weighted average of the 2 axes. Essentially, shitty slerp.
        val newAxis = relative.axis.scalarMultiply(invCompensation).add(absolute.axis.scalarMultiply(compensation))
        val newAngle = relative.angle * invCompensation + absolute.angle * invCompensation
        state = Rotation(newAxis, newAngle)
    }

}