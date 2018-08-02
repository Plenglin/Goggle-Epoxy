package io.github.plenglin.goggle

import org.apache.commons.math3.geometry.euclidean.threed.Rotation
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D


typealias IntPair = Pair<Int, Int>
typealias DoublePair = Pair<Double, Double>

val STD_AXES_TO_ORI = Rotation(Vector3D.MINUS_J, Vector3D.MINUS_K, Vector3D.PLUS_I, Vector3D.PLUS_J)
const val SECONDS_PER_DAY = 86400
