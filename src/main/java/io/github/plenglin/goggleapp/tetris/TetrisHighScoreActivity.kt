package io.github.plenglin.goggleapp.tetris

import io.github.plenglin.goggle.util.ScrollList
import io.github.plenglin.goggle.util.activity.Activity
import io.github.plenglin.goggle.util.input.ButtonInputEvent
import io.github.plenglin.goggle.util.input.EncoderInputEvent
import java.awt.Graphics2D

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
            things.add("${query.getInt(1)} (${query.getString(2)})")
        }

        list = ScrollList(ctx.display.displayBounds, things, ctx.resources.fontSmall)

        ctx.input.listener = {
            when (it) {
                EncoderInputEvent("sel", 1) -> {
                    list.increment()
                }
                EncoderInputEvent("sel", -1) -> {
                    list.decrement()
                }
                ButtonInputEvent("h", true) -> {
                    ctx.activity.popActivity()
                }
            }
        }
    }

    override fun update(dt: Int) {
        list.draw(g)
    }

    override fun stop() {
        g.dispose()
    }

}