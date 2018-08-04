package io.github.plenglin.goggleapp.astronomy

import io.github.plenglin.goggle.Context
import io.github.plenglin.goggle.util.activity.Activity
import io.github.plenglin.goggle.util.app.GoggleApp

class AstronomyApp : GoggleApp {
    override val appName: String = "astronomy"
    override val appLabel: String = "Astronomy"

    override fun onRegistered(ctx: Context) {

    }

    override fun createInitialActivity(): Activity {
        return StarsActivity()
    }

}