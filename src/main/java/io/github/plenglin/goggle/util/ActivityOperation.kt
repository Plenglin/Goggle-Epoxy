package io.github.plenglin.goggle.util

sealed class ActivityOperation

class PushActivityOperation(val newActivity: Activity) : ActivityOperation()

class PopActivityOperation : ActivityOperation()

class SwapActivityOperation(val newActivity: Activity) : ActivityOperation()
