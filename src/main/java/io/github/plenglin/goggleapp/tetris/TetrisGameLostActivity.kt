package io.github.plenglin.goggleapp.tetris

import io.github.plenglin.goggle.util.activity.Activity
import java.awt.Color
import kotlin.math.roundToInt

class TetrisGameLostActivity : Activity() {

    override fun resume() {
        val g = ctx.display.createGraphics()
        g.font = ctx.resources.fontLarge
        val metrics = g.fontMetrics
        val bounds = metrics.getStringBounds(TEXT, g)
        val x = (ctx.display.displayWidth - bounds.width.roundToInt()) / 2
        val ty = (ctx.display.displayHeight + bounds.height.roundToInt()) / 2 - metrics.descent
        val fy = (ctx.display.displayHeight - bounds.height.roundToInt()) / 2

        g.color = Color.black
        g.fillRect(x, fy, bounds.width.roundToInt(), bounds.height.roundToInt())
        g.color = Color.white
        g.drawString(TEXT, x, ty)
    }

    companion object {
        const val TEXT = "YOU LOST"
    }
}