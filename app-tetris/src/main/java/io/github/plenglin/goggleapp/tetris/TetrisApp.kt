package io.github.plenglin.goggleapp.tetris

import io.github.plenglin.goggle.Context
import io.github.plenglin.goggle.util.activity.Activity
import io.github.plenglin.goggle.util.app.GoggleApp

class TetrisApp : GoggleApp {
    override val appName: String = "tetris"
    override val appLabel: String = "Tetris"

    override fun onRegistered(ctx: Context) {
        ctx.db.createStatement().execute("CREATE TABLE IF NOT EXISTS tetris_scores(score integer, received datetime)")
    }
    override fun createInitialActivity(): Activity = TetrisMenuActivity()

}