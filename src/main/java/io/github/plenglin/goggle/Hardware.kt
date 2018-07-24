package io.github.plenglin.goggle

import com.pi4j.io.gpio.GpioController
import io.github.plenglin.goggle.devices.input.Button
import io.github.plenglin.goggle.devices.weather.Altimeter
import io.github.plenglin.goggle.devices.weather.Barometer
import io.github.plenglin.goggle.devices.weather.Thermometer
import io.github.plenglin.goggle.devices.motion.Accelerometer
import io.github.plenglin.goggle.devices.motion.Gyroscope
import io.github.plenglin.goggle.devices.motion.Magnetometer
import io.github.plenglin.goggle.util.scheduler.Command

data class Hardware(
        val gpio: GpioController,
        val acc: Accelerometer,
        val gyro: Gyroscope,
        val mag: Magnetometer,
        val alt: Altimeter,
        val bar: Barometer,
        val therm: Thermometer,
        val commands: List<Command>
)