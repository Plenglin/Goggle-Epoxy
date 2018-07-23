package io.github.plenglin.goggle.util.activity

import io.github.plenglin.goggle.util.scheduler.Command
import java.util.*

class ActivityManager : Command() {

    private val stack: Stack<Activity> = Stack()
    private var currentActivity: Activity? = null
    private var nextActivityOperation: ActivityOperation? = null

    override fun update(dt: Int) {
        val op = nextActivityOperation
        when (op) {
            is PushActivityOperation -> {
                val tmp = currentActivity
                if (tmp != null) {
                    tmp.suspend()
                    stack.push(tmp)
                }
                currentActivity = op.newActivity
                op.newActivity.start()
                op.newActivity.resume()
            }
            is PopActivityOperation -> {
                currentActivity?.apply {
                    suspend()
                    stop()
                }
                val tmp = stack.pop()
                currentActivity = tmp
                tmp?.resume()
            }
            is SwapActivityOperation -> {
                currentActivity?.apply {
                    suspend()
                    stop()
                }
                currentActivity = op.newActivity
                op.newActivity.start()
                op.newActivity.resume()
            }
        }
        nextActivityOperation = null
    }

    fun pushActivity(newActivity: Activity) {
        nextActivityOperation = PushActivityOperation(newActivity)
    }

    fun popActivity() {
        nextActivityOperation = PopActivityOperation()
    }

    fun swapActivity(newActivity: Activity) {
        nextActivityOperation = SwapActivityOperation(newActivity)
    }

}