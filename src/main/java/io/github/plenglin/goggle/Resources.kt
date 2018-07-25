package io.github.plenglin.goggle

import java.awt.Font
import java.io.File

data class Resources(
        val fontPrimary: Font = Font.createFont(Font.TRUETYPE_FONT, File("pixelated.ttf")).deriveFont(8f)
)
