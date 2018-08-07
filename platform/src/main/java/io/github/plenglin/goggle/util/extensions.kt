package io.github.plenglin.goggle.util

import com.pi4j.io.i2c.I2CDevice
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

fun lerperAccurate(a1: Int, a2: Int, b1: Int, b2: Int): (Int) -> Int {
    val db = (b2 - b1)
    val da = (a2 - a1)
    return {db * (it - a1) / da + b1}
}

fun lerper(a1: Double, a2: Double, b1: Double, b2: Double): (Double) -> Double {
    val slope = (b2 - b1) / (a2 - a1)
    return {slope * (it - a1) + b1}
}

fun Int.fixSign(size: Int): Int = if (this > size) this - 2 * size else this

fun I2CDevice.write(addr: Int, byte: Int) {
    write(addr, byte.toByte())
}

private val BYTE_REVERSE_LOOKUP = ByteArray(256) {
    var x = it
    var b = 0
    for (i in 0 until 8) {
        b = (b shl 1) or (x and 1)
        x = x ushr 1
    }
    b.toByte()
}

fun Byte.reversed(): Byte = BYTE_REVERSE_LOOKUP[if (this < 0) this.toInt() + 256 else this.toInt()]

fun main(args: Array<String>) {
    println("${Integer.toBinaryString(0x32)}, ${Integer.toBinaryString(0x32.toByte().reversed().toInt())}")
    println("${Integer.toBinaryString(0xff)}, ${Integer.toBinaryString(0xff.toByte().reversed().toInt())}")
    println("${Integer.toBinaryString(0xe0)}, ${Integer.toBinaryString(0xe0.toByte().reversed().toInt())}")
    println("${Integer.toBinaryString(0x01)}, ${Integer.toBinaryString(0x01.toByte().reversed().toInt())}")
}