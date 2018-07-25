package io.github.plenglin.goggle

import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt

fun main(args: Array<String>) {
    val im = BufferedImage(128, 64, BufferedImage.TYPE_INT_ARGB)

    val g2d = im.createGraphics()
    g2d.color = Color.black
    g2d.fillRect(0, 0, 128, 64)
    g2d.color = Color.white
    g2d.fillRect(10, 10, 10, 10)

    val buf = (im.raster.dataBuffer as DataBufferInt).data
    println("${buf.size} ${buf!!.contentToString()}")
    var x = 0
    var y = 0
    for (i in 0 until buf.size) {
        x++;
        val r = buf[i]
        if (r and 0x00ff0000 != 0) {
            print("1")
        } else {
            print("0")
        }
        if (x == 128) {
            x = 0
            y++
            println()
        }
    }
}