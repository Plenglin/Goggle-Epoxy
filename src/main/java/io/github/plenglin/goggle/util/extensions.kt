package io.github.plenglin.goggle.util

import java.awt.Graphics2D
import java.awt.Rectangle

val Rectangle.x2 get() = x + width
val Rectangle.y2 get() = y + height

fun Graphics2D.clearRect(r: Rectangle) = clearRect(r.x, r.y, r.width, r.height)
fun Graphics2D.fillRect(r: Rectangle) = fillRect(r.x, r.y, r.width, r.height)
