package io.github.plenglin.goggle.devices.motion

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.RealMatrix

class TransformedMagnetometer(private val base: Magnetometer, private val trans: RealMatrix) : Magnetometer {
    override val magneticField: Vector3D
        get() = trans.multiply(MatrixUtils.createColumnRealMatrix(base.magneticField.toArray())).let {
            Vector3D(it.getEntry(0, 0), it.getEntry(1, 0), it.getEntry(2, 0))
        }
}