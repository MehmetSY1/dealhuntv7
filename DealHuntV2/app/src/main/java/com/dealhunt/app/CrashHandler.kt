package com.dealhunt.app

import android.content.Context
import android.content.Intent
import android.os.Process
import kotlin.system.exitProcess

class CrashHandler(private val context: Context) : Thread.UncaughtExceptionHandler {
    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        try {
            val sw = java.io.StringWriter()
            throwable.printStackTrace(java.io.PrintWriter(sw))
            val trace = sw.toString()

            val prefs = context.getSharedPreferences("crash_log", Context.MODE_PRIVATE)
            prefs.edit().putString("last_crash", trace).apply()
        } catch (e: Exception) {
            // yutuluyor - kesinlikle crash handler'in kendisi cokmemeli
        }

        defaultHandler?.uncaughtException(thread, throwable)
        Process.killProcess(Process.myPid())
        exitProcess(1)
    }
}
