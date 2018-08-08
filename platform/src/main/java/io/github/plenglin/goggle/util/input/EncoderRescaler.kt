package io.github.plenglin.goggle.util.input

import io.github.plenglin.goggle.devices.input.Encoder

class EncoderRescaler(val scale: Int, private val base: Encoder) : Encoder {
    override val name: String = base.name
    override val ticks: Long get() = base.ticks / scale
}

fun Encoder.rescale(scale: Int): EncoderRescaler = EncoderRescaler(scale, this)