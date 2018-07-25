package io.github.plenglin.goggle.util.space

import org.apache.commons.math3.geometry.euclidean.threed.Rotation
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.RealMatrix

class OrthographicCamera {
    private lateinit var transform: RealMatrix
    var rotation: Rotation = Rotation.IDENTITY
    var translation: Vector3D = Vector3D.ZERO
    var scale: Double = 1.0

    fun update() {
        val rot = rotation.matrix
        transform = MatrixUtils.createRealMatrix(arrayOf(
                doubleArrayOf(scale * rot[0][0], scale * rot[0][1], scale * rot[0][2], translation.x),
                doubleArrayOf(scale * rot[1][0], scale * rot[1][1], scale * rot[1][2], translation.y),
                doubleArrayOf(scale * rot[2][0], scale * rot[2][1], scale * rot[2][2], translation.z),
                doubleArrayOf(0.0, 0.0, 0.0, 1.0)
        ))
    }

    fun project(world: DoubleArray): DoubleArray {
        val screen = MatrixUtils.createColumnRealMatrix(doubleArrayOf(world[0], world[1], world[2], 1.0))
        val proj = transform.multiply(screen)
        return doubleArrayOf(proj.getEntry(0, 0), proj.getEntry(1, 0), proj.getEntry(2, 0))
    }

}