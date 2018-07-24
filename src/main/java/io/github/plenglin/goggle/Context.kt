package io.github.plenglin.goggle

import io.github.plenglin.goggle.util.OrientationIntegrator
import io.github.plenglin.goggle.util.activity.ActivityManager
import io.github.plenglin.goggle.util.scheduler.Scheduler

class Context(val hardware: Hardware) {

    val scheduler: Scheduler = Scheduler()
    val activity: ActivityManager = ActivityManager()
    val orientation: OrientationIntegrator = OrientationIntegrator(hardware.gyro, hardware.mag, hardware.acc)

    fun run() {
        hardware.commands.forEach(scheduler::addCommand)
        scheduler.addCommand(orientation)
        scheduler.addCommand(activity)

        while (true) {
            scheduler.update()
        }
    }

}