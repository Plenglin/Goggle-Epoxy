package io.github.plenglin.goggle.devices.motion

import org.apache.commons.math3.geometry.euclidean.threed.Rotation


interface Gyroscope {
    /**
     * Returns the change in rotation for a given delta time.
     * @param dt delta time, in milliseconds
     */
    fun getDeltaRotation(dt: Int): Rotation
}