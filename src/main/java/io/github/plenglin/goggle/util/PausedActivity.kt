package io.github.plenglin.goggle.util

import io.github.plenglin.goggle.util.activity.Activity
import io.github.plenglin.goggle.util.input.ButtonInputEvent

class PausedActivity : Activity() {

    override fun resume() {
        ctx.input.listener = {
            if (it is ButtonInputEvent && it.state) {
                ctx.activity.popActivity()
            }
        }
        ctx.display.createGraphics().apply {
            font = ctx.resources.fontLarge
            clearRect(0, 0, ctx.display.displayWidth, ctx.display.displayHeight)
            val width = fontMetrics.stringWidth(TEXT)
            val height = fontMetrics.height
            drawString(TEXT, (ctx.display.displayWidth - width) / 2, (ctx.display.displayHeight + height) / 2)
        }
    }

    companion object {
        const val TEXT = "PAUSED"
    }
}