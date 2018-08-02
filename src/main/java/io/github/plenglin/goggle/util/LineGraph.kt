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
        val yAxTop = metrics.height / 2
        val yAxBot = buf.height - 8 - yAxTop

        val ylerp = lerperAccurate(yIndices.first, yIndices.last, yAxBot, yAxTop)
        log.debug("Lerper: {}, {} -> {}, {}", yIndices.first, yIndices.last, yAxBot, yAxTop)

        val pts = ds.mapIndexed { i, (l, y) ->
            val out = Triple(i * hSpacing + 12, ylerp(y.roundToInt()), l)
            log.debug("{}, {} -> {}", i, y, out.toString())
            out
        }

        // Axes
        g.stroke = BasicStroke(1f)
        g.drawLine(12, yAxTop, 12, yAxBot)
        g.drawLine(buf.width, yAxBot, 12, yAxBot)

        // X Labels
        pts.forEach { (x, _, l) ->
            g.drawString(l, x - metrics.stringWidth(l) / 2, buf.height)
        }

        // Y Labels
        for (t in yIndices) {
            val y = ylerp(t)
            val yl = y + metrics.height / 2
            g.drawString(t.toString(), 0, yl)
            g.drawRect(12, y, 2, 1)
            log.debug("{} at y = {}", t, y)
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