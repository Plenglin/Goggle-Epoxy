package io.github.plenglin.goggleapp.tetris

import io.github.plenglin.goggle.util.ScrollList
import io.github.plenglin.goggle.util.activity.Activity
import io.github.plenglin.goggle.util.input.ButtonInputEvent
import io.github.plenglin.goggle.util.input.EncoderInputEvent
import java.awt.Graphics2D
import java.awt.Rectangle

class TetrisMenuActivity : Activity() {

    private lateinit var g: Graphics2D
    private lateinit var list: ScrollList

    override fun start() {
        g = ctx.hardware.display.createGraphics()
        list = ScrollList(
                Rectangle(50, 40, 10, 30),
                listOf("Start", "Quit"),
                ctx.resources.fontSmall
        )
    }

    override fun resume() {
        ctx.input.listener = {
            when (it) {
                EncoderInputEvent("sel", 1) -> list.increment()
                EncoderInputEvent("sel", -1) -> list.decrement()
                ButtonInputEvent("s", true) -> when (list.selection) {
                    0 -> {
                        ctx.activity.pushActivity(TetrisGameActivity())
                    }
                    1 -> {
                        ctx.activity.popActivity()
                    }
                }
                ButtonInputEvent("h", true) -> ctx.activity.popActivity()
            }
        }
        g.clearRect(0, 0, ctx.hardware.display.displayWidth, ctx.hardware.display.displayHeight)
        g.font = ctx.resources.fontHuge
        g.drawString("TETRIS", 20, 30)
    }

    override fun update(dt: Int) {
        list.draw(g)
    }

}