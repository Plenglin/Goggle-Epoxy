package io.github.plenglin.goggle.activities

import io.github.plenglin.goggle.util.activity.Activity
import io.github.plenglin.goggle.util.app.GoggleApp
import io.github.plenglin.goggle.util.input.ButtonInputEvent
import io.github.plenglin.goggle.util.input.EncoderInputEvent
import org.slf4j.LoggerFactory
import java.awt.Graphics2D

class AppListingActivity : Activity() {

    private val log = LoggerFactory.getLogger(javaClass.name)

    private lateinit var g: Graphics2D
    private lateinit var apps: List<Pair<String, GoggleApp>>
    private var iTop = 0
    private var iSel = 0

    private var redraw = true

    override fun start() {
        g = ctx.hardware.display.createGraphics()
        apps = ctx.appRegistry.listApps().map { (_, app) -> app.appLabel to app }.sortedBy { it.first }
        ctx.input.listener = {
            when (it) {
                ButtonInputEvent("s", true) -> {
                    val a = apps[iSel].second
                    log.debug("User selected app at index {} corresponding to {}", iSel, a)
                    ctx.activity.swapActivity(a.createInitialActivity())
                }
                ButtonInputEvent("h", true) -> {
                    ctx.activity.popActivity()
                }
                EncoderInputEvent("sel", 1) -> {
                    iSel = (iSel + apps.size + 1) % apps.size
                    redraw = true
                }
                EncoderInputEvent("sel", -1) -> {
                    iSel = (iSel + apps.size - 1) % apps.size
                    redraw = true
                }
            }
        }
    }

    override fun update(dt: Int) {
        g.font = ctx.resources.fontPrimary
        val metrics = g.fontMetrics
        val rows = 64 / metrics.height
        if (iSel - iTop >= rows) {
            iTop = iSel - rows + 1
            log.debug("Putting iTop at {}", iTop)
        } else if (iTop > iSel) {
            iTop = iSel
        }

        if (!redraw) {
            return
        }
        log.debug("redrawing")
        g.clearRect(0, 0, 128, 64)

        for (i in 0 until minOf(apps.size, rows)) {
            g.drawString(apps[iTop + i].first, 10, i * metrics.height + metrics.height)
        }
        g.drawString(">", 2, (iSel - iTop) * metrics.height + metrics.height)
        redraw = false
    }

    override fun stop() {
        g.dispose()
    }

}