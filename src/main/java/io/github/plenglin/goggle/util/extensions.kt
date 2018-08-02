package io.github.plenglin.goggle.util

import java.awt.Graphics2D
import java.awt.Rectangle

val Rectangle.x2 get() = x + width
val Rectangle.y2 get() = y + height

fun Graphics2D.clearRect(r: Rectangle) = clearRect(r.x, r.y, r.width, r.height)
fun Graphics2D.fillRect(r: Rectangle) = fillRect(r.x, r.y, r.width, r.height)

fun Double.format(digits: Int): String {
    return "%.${digits}f".format(this)
}

fun Int.lerp(a1: Int, a2: Int, b1: Int, b2: Int): Int  {
    return (b2 - b1) * (this - a1) / (a2 - a1) + b1
}

fun Double.lerp(a1: Double, a2: Double, b1: Double, b2: Double): Double  {
    return (b2 - b1) * (this - a1) / (a2 - a1) + b1
}

fun lerper(a1: Int, a2: Int, b1: Int, b2: Int): (Int) -> Int {
    val slope = (b2 - b1) / (a2 - a1)
    return {slope * (it - a1) + b1}
}

fun lerper(a1: Double, a2: Double, b1: Double, b2: Double): (Double) -> Double {
    val slope = (b2 - b1) / (a2 - a1)
    return {slope * (it - a1) + b1}
}

