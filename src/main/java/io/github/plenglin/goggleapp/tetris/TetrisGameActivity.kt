package io.github.plenglin.goggleapp.tetris

import io.github.plenglin.goggle.commands.PeriodicCommand
import io.github.plenglin.goggle.commands.RunnableCommand
import io.github.plenglin.goggle.util.PausedActivity
import io.github.plenglin.goggle.util.activity.Activity
import io.github.plenglin.goggle.util.input.ButtonInputEvent
import io.github.plenglin.goggle.util.input.EncoderInputEvent
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.util.*

class TetrisGameActivity : Activity() {

    private val log = LoggerFactory.getLogger(javaClass)
    private lateinit var currentGlyph: TetrisGlyph
    private val glyphQueue: Queue<TetrisGlyph> = LinkedList()

    private var gx = 0
    private var gy = 0
    private var buffer = BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_BINARY)
    private var existing = Array(WIDTH) { BooleanArray(HEIGHT) }

    private var canDelta = true
    private var points = 0L
    private lateinit var g: Graphics2D
    private lateinit var periodic: PeriodicCommand

    override fun start() {
        g = ctx.display.createGraphics()
        (1..4).forEach { glyphQueue.offer(getRandomGlyph()) }
        nextGlyph()
    }

    override fun resume() {
        ctx.input.listener = {
            when (it) {
                ButtonInputEvent("h", true) -> {
                    log.info("pausing game")
                    ctx.activity.pushActivity(PausedActivity())
                }
                ButtonInputEvent("x", true) -> {
                    currentGlyph = currentGlyph.rotatedCW
                }
                is EncoderInputEvent -> {
                    if (canDelta) {
                        val prev = gx
                        gx = (gx + it.delta).coerceIn(0, WIDTH - 1 - currentGlyph.width)
                        if (currentGlyph.any { (x, y) -> existing[x + gx][y + gy]}) {
                            log.debug("We cannot move the glyph into this position due to intersection")
                            gx = prev
                        }
                        log.debug("gx: {} -> {}", prev, gx)
                        canDelta = false
                    }
                }
            }
        }

        g.clearRect(0, 0, ctx.display.displayWidth, ctx.display.displayHeight)
        periodic = PeriodicCommand(ctx.scheduler, RunnableCommand {
            redraw()
        }, 250L, 0L)

        ctx.scheduler.addCommand(periodic)
    }

    override fun update(dt: Int) {

    }

    override fun suspend() {
        periodic.stopExecution()
    }

    override fun stop() {
        g.dispose()
    }

    private fun redraw() {
        canDelta = true
        log.debug("redrawing")
        if (hasGlyphHitGround()) {
            log.info("glyph has hit something")
            freezeGlyph()
        }

        gy++
        log.debug("Incrementing gy to {}", gy)

        for (i in 0 until WIDTH) {
            for (j in 0 until HEIGHT) {
                buffer.setRGB(i, j, if (existing[i][j]) WHITE else BLACK)
            }
        }
        currentGlyph.forEach { (x, y) ->
            buffer.setRGB(x + gx, y + gy, WHITE)
        }

        g.drawImage(
                buffer,
                10, 2, 10 + WIDTH * SCALE, 2 + HEIGHT * SCALE,
                0, 0, WIDTH, HEIGHT,
                null)
    }

    private fun hasGlyphHitGround(): Boolean = currentGlyph.any { (x, y) ->
        y + gy == HEIGHT - 1 || existing[x + gx][y + gy + 1]
    }

    private fun getCompleteRows(): List<Int> = (0 until HEIGHT).filter { y ->
        (0 until WIDTH).all { x ->
            buffer.getRGB(x, y) != 0
        }
    }

    private fun freezeGlyph() {
        log.debug("freezing glyph")
        currentGlyph.forEach { (x, y) ->
            existing[x + gx][y + gy] = true
        }
        nextGlyph()
    }

    private fun nextGlyph() {
        log.debug("queueing the next glyph")
        currentGlyph = glyphQueue.poll()
        glyphQueue.offer(getRandomGlyph())
        gx = WIDTH / 2
        gy = 0
    }

    companion object {
        const val HEIGHT = 20
        const val WIDTH = 10
        const val SCALE = 3
        val WHITE = Color.white.rgb
        val BLACK = Color.black.rgb

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
            BASE_GLYPHS.map { listOf(it, it.rotatedCCW, it.rotatedCW, it.rotated180) }.flatten().distinct()
        }

        fun getRandomGlyph(): TetrisGlyph {
            return ALL_GLYPHS[(Math.random() * ALL_GLYPHS.size).toInt()]
        }

    }

}

fun main(args: Array<String>) {
    TetrisGameActivity.ALL_GLYPHS.forEach {
        println(it)
        println(it.toList())
    }
}