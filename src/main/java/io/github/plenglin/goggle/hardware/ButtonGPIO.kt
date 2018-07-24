package io.github.plenglin.goggle.hardware

import com.pi4j.io.gpio.GpioPinDigitalInput
import io.github.plenglin.goggle.devices.input.Button

class ButtonGPIO(val input: GpioPinDigitalInput) : Button {
    override val isPressed: Boolean = input.isHigh

}