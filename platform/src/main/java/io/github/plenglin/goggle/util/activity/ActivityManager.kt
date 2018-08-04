package io.github.plenglin.goggle.util.activity

import io.github.plenglin.goggle.Context
import io.github.plenglin.goggle.util.scheduler.Command
import org.slf4j.LoggerFactory
import java.util.*

class ActivityManager(val ctx: Context) : Command() {

    private val log = LoggerFactory.getLogger(javaClass)

    private val stack: Stack<Activity> = Stack()
    private var currentActivity: Activity? = null
    private var nextActivityOperation: ActivityOperation? = null

    override fun update(dt: Int) {
        val op = nextActivityOperation
        when (op) {
            is PushActivityOperation -> {
                currentActivity?.let {
                    log.info("Pushing {} onto back stack", it)
                    it.suspend()
                    stack.push(it)
                }
                currentActivity = op.newActivity
                op.newActivity.let {
                    log.info("Activating {}", it)
                    it.ctx = this.ctx
                    it.start()
                    it.resume()
                }
            }
            is PopActivityOperation -> {
                currentActivity?.apply {
                    log.info("Stopping {}", this)
                    suspend()
                    stop()
                }
                val tmp = stack.pop()
                log.info("Popped {} from back stack", tmp)
                currentActivity = tmp
                tmp?.resume()
            }
            is SwapActivityOperation -> {
                currentActivity?.apply {
                    log.info("Stopping {}", this)
                    suspend()
                    stop()
                }
                currentActivity = op.newActivity
                op.newActivity.let {
                    log.info("Initializing {}", this)
                    it.ctx = this.ctx
                    it.start()
                    it.resume()
                }
            }
        }
        currentActivity?.update(dt)
        nextActivityOperation = null
    }

    fun pushActivity(newActivity: Activity) {
        nextActivityOperation = PushActivityOperation(newActivity)
        log.info("Queueing {}", nextActivityOperation)
    }

    fun popActivity() {
        nextActivityOperation = PopActivityOperation()
        log.info("Queueing {}", nextActivityOperation)
    }

    fun swapActivity(newActivity: Activity) {
        nextActivityOperation = SwapActivityOperation(newActivity)
        log.info("Queueing {}", nextActivityOperation)
    }

}