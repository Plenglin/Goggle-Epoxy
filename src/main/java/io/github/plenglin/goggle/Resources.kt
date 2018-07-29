package io.github.plenglin.goggle

import java.awt.Font

class Resources {
    private val cl = javaClass.classLoader

    val pixelated = Font.createFont(Font.TRUETYPE_FONT, cl.getResourceAsStream("pixelated.ttf"))
    val fontPrimary: Font = pixelated.deriveFont(8f)
    val fontLarge: Font = pixelated.deriveFont(32f)

}
