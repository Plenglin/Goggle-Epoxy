package io.github.plenglin.goggle.devices.motion

import org.apache.commons.math3.geometry.euclidean.threed.Rotation
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D


const val GYROSCOPE_ZERO_NORM_THRESHOLD = 0.00001

interface Gyroscope {

    val angularVelocity: Vector3D

    /**
     * Returns the change in rotation for a given delta time.
     * @param dt delta time, in milliseconds
     */
    fun getDeltaRotation(dt: Int): Rotation {
        val av = angularVelocity
        val norm = av.norm
        if (norm < GYROSCOPE_ZERO_NORM_THRESHOLD) {
            return Rotation.IDENTITY
        }
        return Rotation(av, norm)
    }
}