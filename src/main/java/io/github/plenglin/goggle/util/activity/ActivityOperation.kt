package io.github.plenglin.goggle.util.activity

sealed class ActivityOperation

data class PushActivityOperation(val newActivity: Activity) : ActivityOperation()

class PopActivityOperation : ActivityOperation()

data class SwapActivityOperation(val newActivity: Activity) : ActivityOperation()
