package io.github.plenglin.goggle.util.input

sealed class InputEvent
data class ButtonInputEvent(val button: String, val state: Boolean) : InputEvent()
data class EncoderInputEvent(val encoder: String, val delta: Int) : InputEvent()
