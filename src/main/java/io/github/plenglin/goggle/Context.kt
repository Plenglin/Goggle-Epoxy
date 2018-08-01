package io.github.plenglin.goggle

import io.github.cdimascio.dotenv.dotenv
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
import java.io.File
import java.sql.DriverManager
import kotlin.concurrent.thread

class Context(val resources: Resources,
              val hardware: Hardware,
              val initialActivity: Activity = BlankActivity(),
              oriCompensation: Double = 0.02,
              val sleepDelay: Long = 0) : AutoCloseable {

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
    val appRegistry: GoggleAppRegistry = GoggleAppRegistry(this)
    val display = hardware.display
    val env = dotenv {
        directory = "."
    }

    val db = DriverManager.getConnection("jdbc:sqlite:epoxy-config.sqlite3")

    private var isShutdown = false

    fun run() {
        log.info("Starting Context {}", this)

        appRegistry.registerApp("io.github.plenglin.goggleapp.astronomy.AstronomyApp")
        appRegistry.registerApp("io.github.plenglin.goggleapp.tetris.TetrisApp")
        appRegistry.registerApp("io.github.plenglin.goggleapp.weather.WeatherForecastApp")

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

        Runtime.getRuntime().addShutdownHook(Thread {
            log.info("Reached shutdown hook. Shutting down.")
            close()
        })

        try {
            while (!isShutdown) {
                scheduler.update()
                if (sleepDelay > 0) {
                    Thread.sleep(sleepDelay)
                }
            }
        } finally {
            log.info("Reached finally block. Shutting down.")
            close()
        }
    }

    override fun close() {
        if (!isShutdown) {
            log.info("Closing {}", this)
            scheduler.terminateAll()
            db.close()
            log.info("Safely closed {}", this)
        } else {
            log.warn("Already closed {}", this)
        }
    }

}