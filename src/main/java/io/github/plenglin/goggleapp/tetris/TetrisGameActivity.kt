package io.github.plenglin.goggleapp.tetris

import io.github.plenglin.goggle.commands.PeriodicCommand
import io.github.plenglin.goggle.commands.RunnableCommand
import io.github.plenglin.goggle.util.PausedActivity
import io.github.plenglin.goggle.util.activity.Activity
import io.github.plenglin.goggle.util.fillRect
import io.github.plenglin.goggle.util.input.ButtonInputEvent
import io.github.plenglin.goggle.util.input.EncoderInputEvent
import io.github.plenglin.goggle.util.x2
import io.github.plenglin.goggle.util.y2
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.sql.Timestamp
import java.util.*
import java.text.SimpleDateFormat



class TetrisGameActivity : Activity() {

    private val log = LoggerFactory.getLogger(javaClass)
    private lateinit var currentGlyph: TetrisGlyph
    private val glyphQueue: Queue<TetrisGlyph> = LinkedList()

    private var gx = 0
    private var gy = 0
    private var gameDrawBuffer = BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_BINARY)
    private var queueDrawBuffer = BufferedImage(4, QUEUE_SIZE * 5, BufferedImage.TYPE_BYTE_BINARY)
    private var existing = Array(WIDTH) { BooleanArray(HEIGHT) }

    private var canDelta = true
    private var points = 0
    private lateinit var g: Graphics2D
    private lateinit var periodic: PeriodicCommand

    private var drawX = 0
    private var drawY = 0
    private lateinit var queueDrawRect: Rectangle

    override fun start() {
        g = ctx.display.createGraphics()
        (1..QUEUE_SIZE).forEach { glyphQueue.offer(getRandomGlyph()) }
        drawX = (ctx.display.displayWidth - REAL_WIDTH) / 2
        drawY = (ctx.display.displayHeight - REAL_HEIGHT) / 2
        queueDrawRect = Rectangle(drawX + REAL_WIDTH + 10, drawY, 4 * SCALE, 5 * QUEUE_SIZE * SCALE)

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
                    log.info("rotating glyph CW")
                    currentGlyph = currentGlyph.rotatedCW
                    while (isGlyphOutOfBoundsLeft()) {
                        gx++
                        log.debug("glyph is OOBL, gx = {}", gx)
                    }
                    while (isGlyphOutOfBoundsRight()) {
                        gx--
                        log.debug("glyph is OOBR, gx = {}", gx)
                    }
                    while (isGlyphIntersecting()) {
                        log.debug("glyph is intersecting, moving to {}", gy)
                        gy--
                    }
                }
                ButtonInputEvent("y", false) -> {
                    dropGlyph()
                }
                is EncoderInputEvent -> {
                    if (canDelta) {
                        val prev = gx
                        gx = (gx + it.delta).coerceIn(0, WIDTH - currentGlyph.width)
                        if (isGlyphIntersecting()) {
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
        }, 400L, 0L)

        ctx.scheduler.addCommand(periodic)
        updatePointCounter()
        updateQueueDrawBuffer()
    }

    private fun isGlyphIntersecting(): Boolean = currentGlyph.any { (x, y) -> existing[x + gx][y + gy]}
    private fun isGlyphOutOfBoundsRight(): Boolean = currentGlyph.width + gx > WIDTH
    private fun isGlyphOutOfBoundsLeft(): Boolean = currentGlyph.width + gx < 0

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
            val result = freezeGlyph()
            if (!result) {
                onGameLost()
            }
            processCompleteRows()
            updatePointCounter()
        }

        gy++
        log.debug("Incrementing gy to {}", gy)

        for (i in 0 until WIDTH) {
            for (j in 0 until HEIGHT) {
                gameDrawBuffer.setRGB(i, j, if (existing[i][j]) WHITE else BLACK)
            }
        }

        currentGlyph.drawToBuffer(gameDrawBuffer, gx, gy)

        g.color = Color.white
        g.fillRect(drawX - 1, drawY - 1, REAL_WIDTH + 2, REAL_HEIGHT + 2)
        g.drawImage(
                gameDrawBuffer,
                drawX, drawY, drawX + REAL_WIDTH, drawY + REAL_HEIGHT,
                0, 0, WIDTH, HEIGHT,
                null)
        g.drawImage(
                queueDrawBuffer,
                queueDrawRect.x, queueDrawRect.y, queueDrawRect.x2, queueDrawRect.y2,
                0, 0, 4, 5 * QUEUE_SIZE,
                null)
    }

    private fun onGameLost() {
        log.info("Game lost! Switching to lost activity")
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val timestamp = Timestamp(System.currentTimeMillis())
        val ts = sdf.format(timestamp)

        val statement = ctx.db.prepareStatement("INSERT INTO tetris_scores (score, received) VALUES (?, ?)")
        statement.setInt(1, points)
        statement.setString(2, ts)
        statement.executeUpdate()

        ctx.activity.swapActivity(TetrisGameLostActivity())
    }

    private fun updatePointCounter() {
        g.color = Color.black
        g.fillRect(0, 0, drawX, ctx.display.displayHeight)
        g.color = Color.white
        g.font = ctx.resources.fontSmall
        g.drawString("Points", 2, 8)
        g.drawString(points.toString(), 2, 16)
    }

    private fun hasGlyphHitGround(): Boolean = currentGlyph.any { (x, y) ->
        y + gy == HEIGHT - 1 || existing[x + gx][y + gy + 1]
    }

    private fun getCompleteRows(): List<Int> = (0 until HEIGHT).filter { y ->
        (0 until WIDTH).all { x ->
            existing[x][y]
        }
    }

    private fun dropGlyph() {
        while (!hasGlyphHitGround()) {
            gy++
        }
    }

    private fun freezeGlyph(): Boolean {
        log.debug("freezing glyph")
        currentGlyph.forEach { (x, y) ->
            existing[x + gx][y + gy] = true
        }
        return nextGlyph()
    }

    private fun processCompleteRows() {
        val rows = getCompleteRows()
        var pointsToAdd = 10
        for (r in rows) {
            log.info("row {} is full", r)
            points += pointsToAdd
            log.info("points + {} = {}", pointsToAdd, points)
            pointsToAdd += 10
            for (y in (1..r).reversed()) {
                val from = y - 1
                log.debug("copying from {} to {}", from, y)
                for (x in 0 until WIDTH) {
                    existing[x][y] = existing[x][from]
                }
            }
        }
    }

    private fun nextGlyph(): Boolean {
        log.debug("queueing the next glyph")
        currentGlyph = glyphQueue.poll()
        glyphQueue.offer(getRandomGlyph())
        gx = (WIDTH - currentGlyph.width) / 2
        gy = 0
        return if (isGlyphIntersecting()) {
            false
        } else {
            updateQueueDrawBuffer()
            true
        }
    }

    private fun updateQueueDrawBuffer() {
        for (x in 0 until queueDrawBuffer.width) {
            for (y in 0 until queueDrawBuffer.height) {
                queueDrawBuffer.setRGB(x, y, BLACK)
            }
        }
        glyphQueue.forEachIndexed { i, g ->
            g.drawToBuffer(queueDrawBuffer, 0, i * 5)
        }
        g.color = Color.black
        g.fillRect(queueDrawRect)
    }

    companion object {
        const val HEIGHT = 20
        const val WIDTH = 10
        const val SCALE = 3
        const val REAL_WIDTH = SCALE * WIDTH
        const val REAL_HEIGHT = SCALE * HEIGHT
        const val QUEUE_SIZE = 4

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
