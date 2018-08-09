package io.github.plenglin.goggle.util

import io.github.plenglin.goggle.devices.motion.Accelerometer
import io.github.plenglin.goggle.devices.motion.Gyroscope
import io.github.plenglin.goggle.devices.motion.Magnetometer
import io.github.plenglin.goggle.util.scheduler.Command
import org.apache.commons.math3.exception.MathArithmeticException
import org.apache.commons.math3.geometry.euclidean.threed.Rotation
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.slf4j.LoggerFactory

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

    private val log = LoggerFactory.getLogger(javaClass)
    private val invCompensation = 1 - compensation

    var orientation: Rotation = Rotation.IDENTITY
        private set

    override fun update(dt: Int) {
        val northRaw = mag.magneticField
        val down = acc.acceleration
        val east = down.crossProduct(northRaw)
        val north = down.crossProduct(east)  // Magnetic dip is dumbo

        val delta = gyro.getDeltaRotation(dt)

        // Dead reckoning orientation.
        val relative = delta.applyTo(orientation)

        // Absolute orientation.
        val absolute: Rotation = try {
            Rotation(Vector3D.MINUS_K, Vector3D.PLUS_J, north, down)
        } catch (e: MathArithmeticException) {  // Sometimes zero norm vector error happens
            log.warn("Failed to convert compass + acceleration to orientation, skipping absolute measurement")
            orientation = relative
            return
        }

        // Weighted average of the 2 rotations. Essentially, shitty slerp.
        val newAxis = relative.axis.scalarMultiply(invCompensation).add(absolute.axis.scalarMultiply(compensation))  // avg. axes
        val newAngle = relative.angle * invCompensation + absolute.angle * compensation  // avg. angles
        orientation = Rotation(newAxis, newAngle)
    }

}