package io.github.plenglin.goggleapp.tetris

import io.github.plenglin.goggle.util.PausedActivity
import io.github.plenglin.goggle.util.activity.Activity
import io.github.plenglin.goggle.util.input.ButtonInputEvent
import java.awt.Graphics2D
import java.awt.image.BufferedImage

class TetrisGameActivity : Activity() {

    private val blocks = Array(WIDTH) { BooleanArray(HEIGHT) }
    private var points = 0L
    private lateinit var g: Graphics2D

    private var currentGlyph: TetrisGlyph? = null
    private var x = 0
    private var y = 0
    private var drawBuffer = BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_BINARY)

    override fun start() {
        g = ctx.display.createGraphics()
    }

    override fun resume() {
        ctx.input.listener = {
            when (it) {
                ButtonInputEvent("x", true) -> ctx.activity.pushActivity(PausedActivity())
            }
        }
    }

    override fun update(dt: Int) {
        g.clearRect(0, 0, ctx.display.displayWidth, ctx.display.displayHeight)
        g.drawRect(10, 10, 30, 30)
    }

    override fun stop() {
        g.dispose()
    }

    companion object {
        const val HEIGHT = 20
        const val WIDTH = 10
        const val SCALE = 3

        val BASE_GLYPHS = listOf(
                TetrisGlyph(
                        arrayOf(
                                booleanArrayOf(true, true, true),
                                booleanArrayOf(true, false, false)
                        )
                ),
                TetrisGlyph(
                        arrayOf(
                                booleanArrayOf(true, true, true),
                                booleanArrayOf(false, false, true)
                        )
                ),
                TetrisGlyph(
                        arrayOf(
                                booleanArrayOf(true, true, true),
                                booleanArrayOf(false, true, false)
                        )
                ),
                TetrisGlyph(
                        arrayOf(
                                booleanArrayOf(true, true, false),
                                booleanArrayOf(false, true, true)
                        )
                ),
                TetrisGlyph(
                        arrayOf(
                                booleanArrayOf(false, true, true),
                                booleanArrayOf(true, true, false)
                        )
                ),
                TetrisGlyph(
                        arrayOf(
                                booleanArrayOf(true, true),
                                booleanArrayOf(true, true)
                        )
                ),
                TetrisGlyph(
                        arrayOf(
                                booleanArrayOf(true, true, true, true)
                        )
                )
        )
        val ALL_GLYPHS by lazy {
            BASE_GLYPHS.map { listOf(it, it.rotatedCCW, it.rotatedCW, it.rotated180) }.flatten()
        }

    }

}