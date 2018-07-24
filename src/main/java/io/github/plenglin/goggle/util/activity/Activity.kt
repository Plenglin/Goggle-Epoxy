package io.github.plenglin.goggle.util.activity

import io.github.plenglin.goggle.Context

/**
 * Represents a window, or a different screen. Inspired somewhat by Android.
 */
abstract class Activity {

    lateinit var ctx: Context

    /**
     * Called once and only once when this activity is created and first initialized.
     */
    open fun start() {}

    /**
     * Called whenever the activity is brought into view, including immediately after initialization.
     */
    open fun resume() {}

    /**
     * Called repeatedly while the activity is in view.
     */
    open fun update(dt: Int) {}

    /**
     * Called whenever the activity is brought out of view.
     */
    open fun suspend() {}

    /**
     * Called when the activity is destroyed and removed from the stack.
     */
    open fun stop() {}

}