package io.github.plenglin.goggle.devices.display

import java.awt.Graphics2D
import java.awt.Rectangle

interface Display {
    val displayWidth: Int
    val displayHeight: Int
    val displayBounds: Rectangle
        get() = Rectangle(0, 0, displayWidth, displayHeight)

    fun createGraphics(): Graphics2D
}