package io.github.plenglin.goggle.emulated

import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*

class DebugSliderPanel : JPanel(GridLayout(1, 0)) {

    fun createControllableObject(name: String, min: Double, max: Double, state: Double): () -> Double {
        val o = SliderObject(name, min, max, state)
        add(o)
        return o::getValue
    }

}

internal class SliderObject(sliderLabel: String, val min: Double, val max: Double, value: Double) : JPanel() {
    val slider: JSlider = JSlider(JSlider.VERTICAL, 0, TICKS, (value * TICKS / (max - min) + 50).toInt())
    val text: JLabel = JLabel(sliderLabel)

    init {
        layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
        add(slider)
        add(text)
    }

    fun getValue(): Double {
        return slider.value * (max - min) / TICKS + min
    }

    override fun getPreferredSize(): Dimension = Dimension(30, 200)

    companion object {
        const val TICKS: Int = 100
    }
}

fun main(args: Array<String>) {
    SwingUtilities.invokeLater {
        println("Creating JFrame")
        val f = JFrame("Goggle Epoxy Test")
        val dsp = DebugSliderPanel()
        f.add(dsp)
        f.isVisible = true
        f.isResizable = false
        println(dsp.createControllableObject("moo", -32.0, 32.0, 30.0)())
        f.pack()
    }
}