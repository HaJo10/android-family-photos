package com.shellmonger.apps.familyphotos.services.mock

import android.util.Log
import com.shellmonger.apps.familyphotos.services.interfaces.AnalyticsService

/**
 * Implementation of the AnalyticsService that outputs data to the console log
 */
class MockAnalyticsService : AnalyticsService {
    companion object {
        private val TAG = this::class.java.simpleName
    }

    /**
     * Records a start session event into the analytics stream
     */
    override fun startSession() {
        Log.v(TAG, "startSession()")
    }

    /**
     * Records a stop session event into the analytics stream
     */
    override fun stopSession() {
        Log.v(TAG, "stopSession()")
    }

    /**
     * Record a custom event into the analytics stream
     *
     * @param name the custom event name
     * @param attributes a list of key-value pairs for recording string attributes
     * @param metrics a list of key-value pairs for recording numeric metrics
     */
    override fun recordEvent(name: String, attributes: Map<String, String>?, metrics: Map<String, Double>?) {
        var event = StringBuilder("")
        attributes?.let { for ((k, v) in it) { event.append(", $k=\"$v\"") } }
        metrics?.let { for ((k, v) in it) { event.append(", $k=${String.format("$.2f", v)}") } }
        if (event.isNotEmpty()) event[0] = ':'
        Log.v(TAG, "recordEvent($name)$event")
    }
}