package io.github.plenglin.goggle

import org.apache.commons.math3.geometry.euclidean.threed.Rotation
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D

fun Rotation.asAngleAxis(): Vector3D {
    return axis.scalarMultiply(angle)
}

fun main(args: Array<String>) {
    val r1 = Rotation.IDENTITY
    val r2 = Rotation(Vector3D.PLUS_J, Vector3D.PLUS_K)
    println(r2)
    println(r1.applyTo(r2).asAngleAxis())
}