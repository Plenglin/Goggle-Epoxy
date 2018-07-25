package io.github.plenglin.goggle

import io.github.plenglin.goggle.devices.motion.Accelerometer
import io.github.plenglin.goggle.devices.motion.Gyroscope
import io.github.plenglin.goggle.devices.motion.Magnetometer
import io.github.plenglin.goggle.devices.weather.Altimeter
import io.github.plenglin.goggle.devices.weather.Barometer
import io.github.plenglin.goggle.devices.weather.Thermometer
import io.github.plenglin.goggle.hardware.*
import org.apache.commons.math3.geometry.euclidean.threed.Rotation
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import javax.swing.JFrame
import javax.swing.SwingUtilities

fun main(args: Array<String>) {
    val mpu = object : Accelerometer, Magnetometer, Gyroscope {
        override val acceleration: Vector3D
            get() = Vector3D(0.0, 1.0, 0.0)

        override val magneticField: Vector3D
            get() = Vector3D(0.0, 0.0, 1.0)

        override fun getDeltaRotation(dt: Int): Rotation = Rotation.IDENTITY/*Rotation(
                Vector3D(1.0, 1.0, 1.0),
                0.01 * dt / 1000.0
        )*/
    }

    val weather = object : Altimeter, Barometer, Thermometer {
        override val altitude: Double
            get() = 300.0
        override val pressure: Double
            get() = 100.0
        override val temperature: Double
            get() = 20.0

    }

    val ssd = DisplaySwingWindow()

    val hw = Hardware(
            acc = mpu, mag = mpu, gyro = mpu,
            alt = weather, bar = weather, therm = weather,
            display = ssd,
            commands = listOf(ssd.updateCommand)
    )

    SwingUtilities.invokeLater {
        println("Creating JFrame")
        val f = JFrame("Goggle Epoxy Test")
        f.add(ssd)
        f.pack()
        f.isVisible = true
        f.isResizable = false
    }
    println("Running context")
    Context(hw).run()
}