package io.github.plenglin.goggle.util

import io.github.plenglin.goggle.Context
import io.github.plenglin.goggle.util.activity.Activity
import io.github.plenglin.goggle.util.app.GoggleApp

class PlaceholderApp(override val appName: String, override val appLabel: String) : GoggleApp {
    override fun onRegistered(ctx: Context) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createInitialActivity(): Activity {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}