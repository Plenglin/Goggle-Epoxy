package io.github.plenglin.goggle.emulated

import io.github.plenglin.goggle.Context
import io.github.plenglin.goggle.Hardware
import io.github.plenglin.goggle.Resources
import io.github.plenglin.goggle.devices.motion.Accelerometer
import io.github.plenglin.goggle.devices.motion.Gyroscope
import io.github.plenglin.goggle.devices.motion.Magnetometer
import io.github.plenglin.goggle.devices.weather.Altimeter
import io.github.plenglin.goggle.devices.weather.Barometer
import io.github.plenglin.goggle.devices.weather.Thermometer
import org.apache.commons.math3.geometry.euclidean.threed.Rotation
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Logger
import java.awt.Point
import java.awt.event.KeyEvent
import javax.swing.JFrame
import javax.swing.SwingUtilities

fun main(args: Array<String>) {
    BasicConfigurator.configure()

    val log = Logger.getLogger("Main")

    val dsp = DebugSliderPanel()
    val accX = dsp.createControllableObject("accX", -10.0, 10.0, 0.0)
    val accY = dsp.createControllableObject("accY", -10.0, 10.0, 10.0)
    val accZ = dsp.createControllableObject("accZ", -10.0, 10.0, 0.0)
    val magX = dsp.createControllableObject("magX", -1.0, 1.0, 0.0)
    val magY = dsp.createControllableObject("magY", -1.0, 1.0, 0.0)
    val magZ = dsp.createControllableObject("magZ", -1.0, 1.0, 0.4)

    val alt = dsp.createControllableObject("alt", 0.0, 1000.0, 300.0)
    val temp = dsp.createControllableObject("temp", -20.0, 40.0, 20.0)
    val pres = dsp.createControllableObject("pres", 50.0, 150.0, 60.0)

    val mpu = object : Accelerometer, Magnetometer, Gyroscope {
        override val acceleration: Vector3D
            get() = Vector3D(
                    accX(),
                    accY(),
                    accZ()
            )

        override val magneticField: Vector3D
            get() = Vector3D(
                    magX(),
                    magY(),
                    magZ()
            )

        override fun getDeltaRotation(dt: Int): Rotation = Rotation.IDENTITY
    }

    val weather = object : Altimeter, Barometer, Thermometer {
        override val altitude: Double
            get() = alt()
        override val pressure: Double
            get() = pres()
        override val temperature: Double
            get() = temp()

    }

    val ssd = DisplaySwingWindow()

    val btnX = ButtonKeyboard("x", KeyEvent.VK_E)
    val btnY = ButtonKeyboard("y", KeyEvent.VK_D)
    val btnZ = ButtonKeyboard("z", KeyEvent.VK_G)
    val btnS = ButtonKeyboard("s", KeyEvent.VK_V)
    val btnH = ButtonKeyboard("h", KeyEvent.VK_H)

    val encSel = EncoderKeyboard("sel", KeyEvent.VK_R, KeyEvent.VK_F)

    val buttons = listOf(btnH, btnS, btnX, btnY, btnZ)
    val encoders = listOf(encSel)

    val hw = Hardware(
            acc = mpu, mag = mpu, gyro = mpu,
            alt = weather, bar = weather, therm = weather,
            display = ssd, buttons = buttons, encoders = encoders,
            commands = listOf(ssd.updateCommand)
    )

    SwingUtilities.invokeLater {
        log.info("Creating Emulated SSD1306")
        JFrame("Goggle Epoxy Test").apply {
            add(ssd)
            pack()
            buttons.forEach(this::addKeyListener)
            encoders.forEach(this::addKeyListener)
            isVisible = true
            isResizable = false
            location = Point(100, 100)
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        }
    }

    SwingUtilities.invokeLater {
        log.info("Creating slider panel")
        JFrame("Sliders").apply {
            add(dsp)
            pack()
            isVisible = true
            location = Point(100, 500)
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        }
    }
    log.info("Running context")
    Context(Resources(), hw, 1.0, 500000).run()
}