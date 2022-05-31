package com.postmyth.a9_kotlinkenny

import android.app.Application
import com.onesignal.OneSignal

const val ONESIGNAL_APP_ID = "8da22719-498e-4857-a8ba-23a3f2a6cf49"

class ApplicationClass : Application() {
    override fun onCreate() {
        super.onCreate()

        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
    }
}