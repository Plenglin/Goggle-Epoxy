package io.github.plenglin.goggle.devices.display

import java.awt.Graphics2D

interface Display {
    fun createGraphics(): Graphics2D
}