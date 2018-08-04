package io.github.plenglin.goggleapp.tetris

import io.github.plenglin.goggle.util.ScrollList
import io.github.plenglin.goggle.util.activity.Activity
import io.github.plenglin.goggle.util.input.ButtonInputEvent
import io.github.plenglin.goggle.util.input.EncoderInputEvent
import java.awt.Graphics2D
import java.awt.Rectangle

class TetrisHighScoreActivity : Activity() {

    private lateinit var g: Graphics2D
    private lateinit var list: ScrollList

    override fun start() {
        g = ctx.display.createGraphics()
    }

    override fun resume() {
        val query = ctx.db.createStatement().executeQuery("SELECT score, received FROM tetris_scores ORDER BY score DESC LIMIT 16")
        val things = mutableListOf<String>()
        while (query.next()) {
            things.add("${query.getInt(1)} at ${query.getString(2)}")
        }
        g.clearRect(0, 0, ctx.display.displayWidth, ctx.display.displayHeight)

        list = ScrollList(Rectangle(0, 8, ctx.display.displayWidth, ctx.display.displayHeight - 8), things, ctx.resources.fontSmall)

        ctx.input.listener = {
            when (it) {
                is EncoderInputEvent -> {
                    list.delta(it.delta)
                }
                ButtonInputEvent("h", true) -> {
                    ctx.activity.popActivity()
                }
            }
        }

        g.font = ctx.resources.fontSmall
        g.drawString("High Scores", 0, 6)
    }

    override fun update(dt: Int) {
        list.draw(g)
    }

    override fun stop() {
        g.dispose()
    }

}