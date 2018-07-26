package io.github.plenglin.goggle.app

class GoggleAppRegistry {

    private val apps = hashMapOf<String, GoggleApp>()

    fun registerApp(app: GoggleApp) {
        apps[app.appName] = app
    }

    fun registerApp(appQualifiedName: String) {
        registerApp(Class.forName(appQualifiedName).newInstance() as GoggleApp)
    }

    fun listApps(): List<Pair<String, GoggleApp>> {
        return apps.toList()
    }

}