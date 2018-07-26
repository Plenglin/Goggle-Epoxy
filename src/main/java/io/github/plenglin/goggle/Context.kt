package io.github.plenglin.goggle

import io.github.plenglin.goggle.activities.BlankActivity
import io.github.plenglin.goggle.commands.ButtonEventQueueFeeder
import io.github.plenglin.goggle.commands.EncoderEventQueueFeeder
import io.github.plenglin.goggle.util.OrientationIntegrator
import io.github.plenglin.goggle.util.TestApp
import io.github.plenglin.goggle.util.activity.ActivityManager
import io.github.plenglin.goggle.util.app.GoggleAppRegistry
import io.github.plenglin.goggle.util.input.InputManager
import io.github.plenglin.goggle.util.scheduler.Scheduler
import org.slf4j.LoggerFactory

class Context(val resources: Resources,
              val hardware: Hardware,
              oriCompensation: Double = 0.02,
              val sleepDelayNanos: Int = 0) {

    val log = LoggerFactory.getLogger(javaClass.name)

    init {
        if (oriCompensation > 0.1) {
            log.warn("Warning! Orientation compensation is a very high {}. This may result in an unstable headset, " +
                    "unless you know what you are doing.", oriCompensation)
        }
    }

    val scheduler: Scheduler = Scheduler()
    val activity: ActivityManager = ActivityManager(this)
    val input: InputManager = InputManager()
    val orientation: OrientationIntegrator = OrientationIntegrator(hardware.gyro, hardware.mag, hardware.acc, oriCompensation)
    val appRegistry: GoggleAppRegistry = GoggleAppRegistry()

    fun run() {
        log.info("Beginning Context {}", this)

        appRegistry.registerApp("io.github.plenglin.goggleapp.astronomy.AstronomyApp")
        appRegistry.registerApp("io.github.plenglin.goggleapp.tetris.TetrisApp")
        appRegistry.registerApp(TestApp("asdf1", "fads"))
        appRegistry.registerApp(TestApp("asdf2", "gasd"))
        appRegistry.registerApp(TestApp("asdf3", "hfegrt"))
        appRegistry.registerApp(TestApp("asdf4", "fghjg"))
        appRegistry.registerApp(TestApp("asdf5", "fherh"))
        appRegistry.registerApp(TestApp("asdf6", "trtet"))
        appRegistry.registerApp(TestApp("asdf7", "fndfh"))
        appRegistry.registerApp(TestApp("asdf8", "urewiof"))
        appRegistry.registerApp(TestApp("asdf9", "jbdsow"))

        hardware.commands.forEach(scheduler::addCommand)
        hardware.buttons.forEach {
            scheduler.addCommand(ButtonEventQueueFeeder(it, input.queue))
        }
        hardware.encoders.forEach {
            scheduler.addCommand(EncoderEventQueueFeeder(it, input.queue))
        }
        scheduler.addCommand(orientation)
        scheduler.addCommand(input)
        scheduler.addCommand(activity)

        activity.pushActivity(BlankActivity())

        while (true) {
            scheduler.update()
            if (sleepDelayNanos > 0) {
                Thread.sleep(0, sleepDelayNanos)
            }
        }
    }

}