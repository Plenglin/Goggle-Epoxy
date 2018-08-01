package io.github.plenglin.goggle.activities

import io.github.plenglin.goggle.util.ScrollList
import io.github.plenglin.goggle.util.activity.Activity
import io.github.plenglin.goggle.util.app.GoggleApp
import io.github.plenglin.goggle.util.input.ButtonInputEvent
import io.github.plenglin.goggle.util.input.EncoderInputEvent
import org.slf4j.LoggerFactory
import java.awt.Graphics2D

class AppListingActivity : Activity() {

    private val log = LoggerFactory.getLogger(javaClass)

    private lateinit var g: Graphics2D
    private lateinit var scroll: ScrollList
    lateinit var apps: List<GoggleApp>

    override fun start() {
        g = ctx.hardware.display.createGraphics()
        val appReg = ctx.appRegistry.listApps().sortedBy { it.second.appLabel }
        val appNames = appReg.map { it.second.appLabel }
        apps = appReg.map { it.second }
        scroll = ScrollList(ctx.hardware.display.displayBounds, appNames, ctx.resources.fontSmall)
    }

    override fun resume() {
        ctx.input.listener = {
            when (it) {
                ButtonInputEvent("s", true) -> {
                    val a = apps[scroll.selection]
                    log.debug("User selected app at index {} corresponding to {}", scroll.selection, a)
                    ctx.activity.swapActivity(a.createInitialActivity())
                }
                ButtonInputEvent("h", true) -> {
                    ctx.activity.popActivity()
                }
                is EncoderInputEvent -> {
                    scroll.delta(it.delta)
                }
            }
        }
    }

    override fun update(dt: Int) {
        scroll.draw(g)
    }

    override fun stop() {
        g.dispose()
    }

}