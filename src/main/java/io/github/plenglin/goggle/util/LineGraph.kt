package io.github.plenglin.goggle.util

import org.slf4j.LoggerFactory
import java.awt.BasicStroke
import java.awt.Font
import java.awt.image.BufferedImage
import kotlin.math.roundToInt

class LineGraph(private val xs: List<String>, private val ys: List<Double>, val yIndices: IntProgression) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun drawTo(buf: BufferedImage, font: Font) {
        assert(xs.size == ys.size) { "xs not same length as ys!" }
        val ds = xs.zip(ys)
        val g = buf.createGraphics()
        g.clearRect(0, 0, buf.width, buf.height)
        g.font = font
        val metrics = g.fontMetrics

        val hSpacing = buf.width / xs.size
        val yAxBot = buf.height - 8

        val tempToYCoord = lerper(yIndices.first, yIndices.last, yAxBot, 0)

        val pts = ds.mapIndexed { i, (l, y) ->
            val out = Triple(i * hSpacing + 12, tempToYCoord(y.roundToInt()), l)
            log.debug("{}, {} -> {}", i, y, out.toString())
            out
        }

        // Axes
        g.stroke = BasicStroke(1f)
        g.drawLine(12, 0, 12, yAxBot)
        g.drawLine(buf.width, yAxBot, 12, yAxBot)

        // X Labels
        pts.forEach { (x, _, l) ->
            g.drawString(l, x - metrics.stringWidth(l) / 2, buf.height)
        }

        // Y Labels
        for (t in yIndices) {
            g.drawString(t.toString(), 0, tempToYCoord(t))
        }

        // Data
        g.stroke = BasicStroke(1.1f)
        var (x1, y1) = pts.first()
        for ((x2, y2) in pts.drop(1)) {
            g.drawLine(x1, y1, x2, y2)
            x1 = x2
            y1 = y2
        }
        g.dispose()
    }
}