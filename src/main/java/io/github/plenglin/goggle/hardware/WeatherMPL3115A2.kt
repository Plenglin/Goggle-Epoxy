package io.github.plenglin.goggle.hardware

import com.pi4j.io.i2c.I2CDevice
import io.github.plenglin.goggle.devices.weather.Altimeter
import io.github.plenglin.goggle.devices.weather.Barometer
import io.github.plenglin.goggle.devices.weather.Thermometer
import io.github.plenglin.goggle.util.scheduler.Command

class WeatherMPL3115A2(val dev: I2CDevice, val addr: Int) : Command(), Altimeter, Barometer, Thermometer {
    override var altitude: Double = 0.0
        private set
        get() {
            if (!isAltThmUpdated) {
                updateAltThm()
            }
            return field
        }
    override var temperature: Double = 0.0
        private set
        get() {
            if (!isAltThmUpdated) {
                updateAltThm()
            }
            return field
        }
    override var pressure: Double = 0.0
        private set
        get() {
            if (!isBarUpdated) {
                updateAltThm()
            }
            return field
        }

    private var isAltThmUpdated = false
    private var isBarUpdated = false

    override fun update(dt: Int) {
        isAltThmUpdated = false
        isBarUpdated = false
    }

    private fun updateAltThm() {
        dev.write(addr, byteArrayOf(0x26, 0xB9.toByte()));
        val buf = ByteArray(6)
        dev.read(addr, buf, 0, 6)
        val rawAltitude = (buf[1].toInt() shl 16) or (buf[2].toInt() shl 8) or (buf[3].toInt())
        val rawTemp = (buf[4].toInt() shl 8) or buf[5].toInt()
        altitude = rawAltitude / 256.0;
        temperature = rawTemp / 256.0;
        isAltThmUpdated = true
    }

    private fun updateBar() {
        dev.write(addr, byteArrayOf(0x26, 0x39.toByte()));
        val buf = ByteArray(4)
        dev.read(addr, buf, 0, 4)
        val rawPressure = (buf[1].toInt() shl 16) or (buf[2].toInt() shl 8) or (buf[3].toInt())
        pressure = rawPressure / 64.0
        isBarUpdated = true
    }
}
