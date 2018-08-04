package io.github.plenglin.goggle

import java.awt.Font

class Resources {
    private val cl = javaClass.classLoader

    val pixelated = Font.createFont(Font.TRUETYPE_FONT, cl.getResourceAsStream("pixelated.ttf"))
    val fontSmall: Font = pixelated.deriveFont(8f)
    val fontMedium: Font = pixelated.deriveFont(16f)
    val fontLarge: Font = pixelated.deriveFont(24f)
    val fontHuge: Font = pixelated.deriveFont(32f)

}
