package com.dealhunt.app

import android.content.Context
import android.os.Process
import kotlin.system.exitProcess

class CrashHandler(private val context: Context) : Thread.UncaughtExceptionHandler {
    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        try {
            val sw = java.io.StringWriter()
            throwable.printStackTrace(java.io.PrintWriter(sw))
            val trace = "THREAD: ${thread.name}\n${sw}"

            // commit() senkron yazar - apply() asenkron oldugu icin process olurse kaybolabilir
            val prefs = context.getSharedPreferences("crash_log", Context.MODE_PRIVATE)
            prefs.edit().putString("last_crash", trace).commit()

            // Yedek olarak dosyaya da yaz
            try {
                val file = java.io.File(context.filesDir, "crash.txt")
                file.writeText(trace)
            } catch (_: Exception) {}
        } catch (e: Exception) {
            // yutuluyor - kesinlikle crash handler'in kendisi cokmemeli
        }

        defaultHandler?.uncaughtException(thread, throwable)
        Process.killProcess(Process.myPid())
        exitProcess(1)
    }
}
