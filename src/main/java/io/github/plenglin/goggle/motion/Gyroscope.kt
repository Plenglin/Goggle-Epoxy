package io.github.plenglin.goggle.motion

import org.apache.commons.math3.geometry.euclidean.threed.Rotation


interface Gyroscope {
    fun getDeltaRotation(dt: Int): Rotation
}