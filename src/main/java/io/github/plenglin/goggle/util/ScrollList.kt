package io.github.plenglin.goggle.util

import org.slf4j.LoggerFactory
import java.awt.Font
import java.awt.Graphics
import java.awt.Rectangle

class ScrollList(bounds: Rectangle, val things: List<String>, val font: Font, val indicator: Char = '>') {
    private val log = LoggerFactory.getLogger(javaClass)
    var bounds: Rectangle = bounds
        set(value) {
            field = value
            redraw = true
        }

    private var iTop = 0
    private var iSel = 0
    private var redraw = true

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

    fun draw(g: Graphics) {
        g.font = font
        val metrics = g.fontMetrics
        val rows = bounds.height / metrics.height
        if (iSel - iTop >= rows) {
            iTop = iSel - rows + 1
            log.debug("Putting iTop at {}", iTop)
        } else if (iTop > iSel) {
            iTop = iSel
            log.debug("Putting iTop at {}", iTop)
        }

        if (!redraw) {
            return
        }
        log.debug("redrawing")
        g.clearRect(bounds.x, bounds.y, bounds.width, bounds.height)

        val indWidth = metrics.charWidth(indicator)
        for (i in 0 until minOf(things.size, rows)) {
            g.drawString(things[iTop + i], bounds.x + indWidth + 2, bounds.y + i * metrics.height + metrics.height)
        }
        g.drawString(indicator.toString(), bounds.x + 2, bounds.y + (iSel - iTop) * metrics.height + metrics.height)
        redraw = false
    }

}