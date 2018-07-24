package io.github.plenglin.goggle.hardware

import com.pi4j.io.gpio.GpioPinDigitalInput
import io.github.plenglin.goggle.devices.input.Encoder

class EncoderGPIO(val a: GpioPinDigitalInput, val b: GpioPinDigitalInput) : Encoder {
    override val ticks: Double
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.


}