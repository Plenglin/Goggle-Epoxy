package io.github.plenglin.goggle.util.app

sealed class AppDef

data class QualifiedAppDef(val name: String) : AppDef()
data class StandardAppDef(val app: GoggleApp) : AppDef()
