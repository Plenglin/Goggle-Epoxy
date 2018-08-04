package io.github.plenglin.goggle.util.app

import io.github.plenglin.goggle.Context

class GoggleAppRegistry(val ctx: Context) {

    private val apps = hashMapOf<String, GoggleApp>()

    fun registerApp(app: GoggleApp) {
        apps[app.appName] = app
        app.onRegistered(ctx)
    }

    fun registerApp(appQualifiedName: String) {
        registerApp(Class.forName(appQualifiedName).newInstance() as GoggleApp)
    }

    fun listApps(): List<Pair<String, GoggleApp>> {
        return apps.toList()
    }

}