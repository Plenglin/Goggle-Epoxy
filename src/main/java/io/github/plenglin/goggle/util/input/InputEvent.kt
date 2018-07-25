package io.github.plenglin.goggle.util.input

import io.github.plenglin.goggle.devices.input.Button
import io.github.plenglin.goggle.devices.input.Encoder

sealed class InputEvent(val source: String)
data class ButtonInputEvent(val button: Button, val state: Boolean) : InputEvent(button.name)
data class EncoderInputEvent(val encoder: Encoder, val delta: Int) : InputEvent(encoder.name)
