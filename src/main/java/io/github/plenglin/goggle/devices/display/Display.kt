package io.github.plenglin.goggle.devices.display

import java.awt.Graphics2D

interface Display {
    val displayWidth: Int
    val displayHeight: Int
    fun createGraphics(): Graphics2D
}