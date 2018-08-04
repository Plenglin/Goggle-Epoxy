package io.github.plenglin.goggle.devices.motion

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D


interface Magnetometer {
    /**
     * The magnetic field strength, in gauss.
     */
    val magneticField: Vector3D
}