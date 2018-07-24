package io.github.plenglin.goggle.activities

import io.github.plenglin.goggle.util.activity.Activity
import java.awt.Color

class BlankActivity : Activity() {

    override fun resume() {
        val g = ctx.hardware.display.createGraphics()
        g.background = Color.black
        g.clearRect(0, 0, 128, 64)
        g.dispose()
    }

}