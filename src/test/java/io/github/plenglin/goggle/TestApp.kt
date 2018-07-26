package io.github.plenglin.goggle

import io.github.plenglin.goggle.app.GoggleApp
import io.github.plenglin.goggle.util.activity.Activity

class TestApp : GoggleApp {
    override val appName: String = "test"
    override val appLabel: String = "test"

    override fun createInitialActivity(): Activity {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

fun main(args: Array<String>) {
    val start = System.currentTimeMillis()
    //println(GoggleAppRegistry().apps)
    //println(System.currentTimeMillis() - start)
    println(Class.forName("io.github.plenglin.goggle.TestApp").newInstance())
}