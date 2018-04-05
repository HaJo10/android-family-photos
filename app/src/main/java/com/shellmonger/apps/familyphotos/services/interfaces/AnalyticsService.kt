package com.shellmonger.apps.familyphotos.services.interfaces

interface AnalyticsService {
    /**
     * Records a start session event into the analytics stream
     */
    fun startSession()

    /**
     * Records a stop session event into the analytics stream
     */
    fun stopSession()

    /**
     * Record a custom event into the analytics stream
     *
     * @param name the custom event name
     * @param attributes a list of key-value pairs for recording string attributes
     * @param metrics a list of key-value pairs for recording numeric metrics
     */
    fun recordEvent(name: String, attributes: Map<String,String>? = null, metrics: Map<String,Double>? = null)
}