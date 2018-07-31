package io.github.plenglin.goggle

import io.github.plenglin.goggle.activities.BlankActivity
import io.github.plenglin.goggle.commands.ButtonEventQueueFeeder
import io.github.plenglin.goggle.commands.EncoderEventQueueFeeder
import io.github.plenglin.goggle.util.OrientationIntegrator
import io.github.plenglin.goggle.util.activity.Activity
import io.github.plenglin.goggle.util.activity.ActivityManager
import io.github.plenglin.goggle.util.app.GoggleAppRegistry
import io.github.plenglin.goggle.util.input.InputManager
import io.github.plenglin.goggle.util.scheduler.Scheduler
import org.slf4j.LoggerFactory

class Context(val resources: Resources,
              val hardware: Hardware,
              val initialActivity: Activity = BlankActivity(),
              oriCompensation: Double = 0.02,
              val sleepDelay: Long = 0) {

    val log = LoggerFactory.getLogger(javaClass)

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
    val display = hardware.display

    //val db =

    fun run() {
        log.info("Beginning Context {}", this)

        appRegistry.registerApp("io.github.plenglin.goggleapp.astronomy.AstronomyApp")
        appRegistry.registerApp("io.github.plenglin.goggleapp.tetris.TetrisApp")

        /*for (i in 0..10) {
            appRegistry.registerApp(PlaceholderApp("ph-$i", "Placeholder $i"))
        }*/

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

        activity.pushActivity(initialActivity)

        while (true) {
            scheduler.update()
            if (sleepDelay > 0) {
                Thread.sleep(sleepDelay)
            }
        }
    }

}