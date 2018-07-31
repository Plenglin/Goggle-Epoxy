package io.github.plenglin.goggle.util

import org.slf4j.LoggerFactory
import java.awt.*

class ScrollList(bounds: Rectangle,
                 private val things: List<String>,
                 private val font: Font,
                 private val indicator: Char = '>',
                 private val scrollWidth: Int = 2) {

    private val log = LoggerFactory.getLogger(javaClass)
    var bounds: Rectangle = bounds
        set(value) {
            field = value
            redraw = true
        }

    private var iTop = 0
    private var iSel = 0
    private var redraw = true
    private val metrics = Canvas().getFontMetrics(font)
    private val rows get() = bounds.height / metrics.height
    private val barStart get() = bounds.y + bounds.height * iTop / things.size
    private val barHeight get() = bounds.height * rows / things.size

    val selection get() = iSel

    fun increment() {
        iSel = (iSel + things.size + 1) % things.size
        log.debug("Selected: {}", iSel)
        redraw = true
    }

    fun decrement() {
        iSel = (iSel + things.size - 1) % things.size
        log.debug("Selected: {}", iSel)
        redraw = true
    }

    fun forceRedraw() {
        redraw = true
    }

    fun draw(g: Graphics) {
        if (!redraw) {
            return
        }
        g.font = font
        if (iSel - iTop >= rows) {
            iTop = iSel - rows + 1
            log.debug("Putting iTop at {}", iTop)
        } else if (iTop > iSel) {
            iTop = iSel
            log.debug("Putting iTop at {}", iTop)
        }

        log.debug("redrawing")
        g.clearRect(bounds.x, bounds.y, bounds.width, bounds.height)

        val indWidth = metrics.charWidth(indicator)
        for (i in 0 until minOf(things.size, rows)) {
            g.drawString(things[iTop + i], bounds.x + indWidth + 2, bounds.y + i * metrics.height + metrics.height)
        }
        g.drawString(indicator.toString(), bounds.x + 2, bounds.y + (iSel - iTop) * metrics.height + metrics.height)

        if (barHeight < bounds.height) {
            log.debug("Drawing scrollbar")
            g.fillRect(bounds.width - scrollWidth - 1, barStart, bounds.width, barHeight)
        }

        redraw = false
    }

}