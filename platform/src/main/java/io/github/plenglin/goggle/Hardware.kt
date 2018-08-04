package io.github.plenglin.goggle

import com.pi4j.io.gpio.GpioController
import com.pi4j.io.i2c.I2CBus
import io.github.plenglin.goggle.devices.GPS
import io.github.plenglin.goggle.devices.display.Display
import io.github.plenglin.goggle.devices.input.Button
import io.github.plenglin.goggle.devices.input.Encoder
import io.github.plenglin.goggle.devices.motion.Accelerometer
import io.github.plenglin.goggle.devices.motion.Gyroscope
import io.github.plenglin.goggle.devices.motion.Magnetometer
import io.github.plenglin.goggle.devices.weather.Altimeter
import io.github.plenglin.goggle.devices.weather.Barometer
import io.github.plenglin.goggle.devices.weather.Thermometer
import io.github.plenglin.goggle.util.GPSEmulatedStationary
import io.github.plenglin.goggle.util.scheduler.Command

class Hardware(
        val gpio: GpioController? = null,
        val i2c: I2CBus? = null,
        val acc: Accelerometer,
        val gyro: Gyroscope,
        val mag: Magnetometer,
        val alt: Altimeter,
        val bar: Barometer,
        val therm: Thermometer,
        val display: Display,
        gps: GPS? = null,
        val buttons: List<Button> = emptyList(),
        val encoders: List<Encoder> = emptyList(),
        val commands: List<Command> = emptyList()
) {
    val gps: GPS = gps ?: GPSEmulatedStationary(0.0, 0.0)
}