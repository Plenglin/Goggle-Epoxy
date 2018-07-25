package io.github.plenglin.goggle.devices.motion

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D

@FunctionalInterface
interface Accelerometer {
    /**
     * The acceleration, in m/s/s.
     */
    val acceleration: Vector3D
}