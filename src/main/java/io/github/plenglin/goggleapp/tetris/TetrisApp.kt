package io.github.plenglin.goggleapp.tetris

import io.github.plenglin.goggle.util.activity.Activity
import io.github.plenglin.goggle.util.app.GoggleApp

class TetrisApp : GoggleApp {
    override val appName: String = "tetris"
    override val appLabel: String = "Tetris"

    override fun createInitialActivity(): Activity = TetrisMenuActivity()

}