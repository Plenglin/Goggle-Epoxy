package io.github.plenglin.goggle

import io.github.plenglin.goggle.activities.BlankActivity
import io.github.plenglin.goggle.activities.HomeSensorsActivity
import io.github.plenglin.goggle.commands.ButtonEventQueueFeeder
import io.github.plenglin.goggle.commands.EncoderEventQueueFeeder
import io.github.plenglin.goggle.util.OrientationIntegrator
import io.github.plenglin.goggle.util.activity.ActivityManager
import io.github.plenglin.goggle.util.input.InputManager
import io.github.plenglin.goggle.util.scheduler.Scheduler

class Context(val resources: Resources, val hardware: Hardware) {

    val scheduler: Scheduler = Scheduler()
    val activity: ActivityManager = ActivityManager(this)
    val input: InputManager = InputManager()
    // TODO: CHANGE COMPENSATION WHEN USING EMBEDDED
    val orientation: OrientationIntegrator = OrientationIntegrator(hardware.gyro, hardware.mag, hardware.acc, 0.5)

    fun run() {
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
            Thread.sleep(1)
        }
    }

}