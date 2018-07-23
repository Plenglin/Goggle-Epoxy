package io.github.plenglin.goggle.motion

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D


interface Magnetometer {
    fun getMagneticField(): Vector3D
}