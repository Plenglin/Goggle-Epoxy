package io.github.plenglin.goggle.activities

import io.github.plenglin.goggle.util.activity.Activity
import io.github.plenglin.goggle.util.input.ButtonInputEvent
import java.awt.Color

class BlankActivity : Activity() {

    override fun resume() {
        val g = ctx.hardware.display.createGraphics()
        g.background = Color.black
        g.clearRect(0, 0, 128, 64)
        g.dispose()

        ctx.input.listener = {
            if (it is ButtonInputEvent && it.state) {
                ctx.activity.pushActivity(HomeSensorsActivity())
            }
        }
    }

    override fun suspend() {
        ctx.input.listener = {}
    }

}