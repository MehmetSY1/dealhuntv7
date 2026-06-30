package com.dealhunt.app

import android.app.Application
import android.os.StrictMode

class DealHuntApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // StrictMode'u tamamen kapat - network/disk ana thread kisitlamasi olmasin
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.LAX)
        StrictMode.setVmPolicy(StrictMode.VmPolicy.LAX)

        Thread.setDefaultUncaughtExceptionHandler(CrashHandler(this))
    }
}
