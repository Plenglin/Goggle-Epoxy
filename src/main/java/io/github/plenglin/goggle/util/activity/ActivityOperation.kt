package io.github.plenglin.goggle.util.activity

sealed class ActivityOperation

class PushActivityOperation(val newActivity: Activity) : ActivityOperation()

class PopActivityOperation : ActivityOperation()

class SwapActivityOperation(val newActivity: Activity) : ActivityOperation()
