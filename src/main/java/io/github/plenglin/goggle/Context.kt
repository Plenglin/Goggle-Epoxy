package io.github.plenglin.goggle

import io.github.plenglin.goggle.activities.HomeSensorsActivity
import io.github.plenglin.goggle.util.OrientationIntegrator
import io.github.plenglin.goggle.util.activity.ActivityManager
import io.github.plenglin.goggle.util.scheduler.Scheduler

class Context(val resources: Resources, val hardware: Hardware) {

    val scheduler: Scheduler = Scheduler()
    val activity: ActivityManager = ActivityManager(this)
    // TODO: CHANGE COMPENSATION WHEN USING EMBEDDED
    val orientation: OrientationIntegrator = OrientationIntegrator(hardware.gyro, hardware.mag, hardware.acc, 0.5)

    fun run() {
        hardware.commands.forEach(scheduler::addCommand)
        scheduler.addCommand(orientation)
        scheduler.addCommand(activity)

        activity.pushActivity(HomeSensorsActivity())

        while (true) {
            scheduler.update()
            Thread.sleep(1)
        }
    }

}