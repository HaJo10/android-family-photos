package com.shellmonger.apps.familyphotos.lifecycle

import android.app.Application

/**
 * Application wrapper.  Used to record the start time of the application as
 * the application wrapper is the first class to be instantiated.
 */
class ApplicationWrapper : Application() {
    companion object {
        val startTime = System.currentTimeMillis()
    }
}