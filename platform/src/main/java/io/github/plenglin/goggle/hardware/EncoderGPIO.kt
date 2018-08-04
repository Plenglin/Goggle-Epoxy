package io.github.plenglin.goggle.hardware

import com.pi4j.io.gpio.GpioPinDigitalInput
import io.github.plenglin.goggle.devices.input.Encoder
import io.github.plenglin.goggle.util.scheduler.Command

class EncoderGPIO(override val name: String, val a: GpioPinDigitalInput, val b: GpioPinDigitalInput) : Command(), Encoder {
    override var ticks: Long = 0
        private set

    private var pa: Boolean = false
    private var pb: Boolean = false

    override fun update(dt: Int) {
        val cb = a.isHigh
        val ca = b.isHigh

        if (pa) {
            if (pb) {  // A high, B high
                if (!ca) {
                    ticks++
                } else if (!cb) {
                    ticks--
                }
            } else {  // A high, B low
                if (cb) {
                    ticks++
                } else if (!ca) {
                    ticks--
                }
            }
        } else {
            if (pb) {  // A low, B high
                if (!cb) {
                    ticks++
                } else if (ca) {
                    ticks--
                }
            } else {  // A low, B low
                if (ca) {
                    ticks++
                } else if (cb) {
                    ticks--
                }
            }
        }

        pa = ca
        pb = cb
    }
}