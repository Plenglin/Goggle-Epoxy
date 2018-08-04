package io.github.plenglin.goggle.util.space

import io.github.plenglin.goggle.DoublePair
import org.apache.commons.math3.geometry.euclidean.threed.Rotation
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D
import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.RealMatrix

class PerspectiveCamera {
    private lateinit var transform: RealMatrix
    var rotation: Rotation = Rotation.IDENTITY
    var translation: Vector3D = Vector3D.ZERO
    var postTranslation: Vector2D = Vector2D.ZERO
    var postScale: Double = 0.0
    var projectionRadiusX: Double = 0.0
    var projectionRadiusY: Double = 0.0

    fun update() {
        val rot = rotation.matrix
        transform = MatrixUtils.createRealMatrix(arrayOf(
                doubleArrayOf(rot[0][0], rot[0][1], rot[0][2], translation.x),
                doubleArrayOf(rot[1][0], rot[1][1], rot[1][2], translation.y),
                doubleArrayOf(rot[2][0], rot[2][1], rot[2][2], translation.z),
                doubleArrayOf(0.0, 0.0, 0.0, 1.0)
        ))
    }

    fun projectOrtho(world: DoubleArray): DoubleArray {
        val screen = MatrixUtils.createColumnRealMatrix(doubleArrayOf(world[0], world[1], world[2], 1.0))
        val proj = transform.multiply(screen)
        return doubleArrayOf(proj.getEntry(0, 0), proj.getEntry(1, 0), proj.getEntry(2, 0))
    }

    inline fun draw(vararg world: DoubleArray, op: (List<DoublePair>) -> Unit) {
        val ortho = world.map { projectOrtho(it) }
        val z = ortho.map { it[2] }
        if (z.any { it > 0 }) {  // Cull behind
            val x = ortho.mapIndexed { i, v -> v[0] / z[i] }
            val y = ortho.mapIndexed { i, v -> v[1] / z[i] }
            if (Math.abs(x[0]) < projectionRadiusX && Math.abs(y[0]) < projectionRadiusY) {  // Cull out of bounds
                //println("$x, $y, $z")
                op(x.map { it * postScale + postTranslation.x }.zip(y.map { it * postScale + postTranslation.y }))
            }
        }
    }

}