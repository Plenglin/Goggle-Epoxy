package io.github.plenglin.goggle.hardware

import com.pi4j.io.gpio.GpioPinDigitalInput
import io.github.plenglin.goggle.devices.input.Button

class ButtonGPIO(override val name: String, val input: GpioPinDigitalInput, val highPressed: Boolean = true) : Button {
    override val isPressed: Boolean get() = input.isLow == highPressed
}