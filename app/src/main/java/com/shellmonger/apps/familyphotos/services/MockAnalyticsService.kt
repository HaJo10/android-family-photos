package com.shellmonger.apps.familyphotos.services

import android.util.Log

class MockAnalyticsService : AnalyticsService {
    val TAG = this::class.java.simpleName

    override fun startSession() {
        Log.d(TAG, "startSession")
    }

    override fun stopSession() {
        Log.d(TAG, "stopSession")
    }

    override fun recordEvent(type: String) {
        Log.d(TAG, "recordEvent($type)")
    }
}