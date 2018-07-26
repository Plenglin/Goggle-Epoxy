package io.github.plenglin.goggle

import java.awt.Font

class Resources() {
    private val cl = javaClass.classLoader

    val fontPrimary: Font = Font.createFont(Font.TRUETYPE_FONT, cl.getResourceAsStream("pixelated.ttf")).deriveFont(8f)
}
