package com.shellmonger.apps.familyphotos.services

interface AnalyticsService {
    fun startSession()

    fun stopSession()

    fun recordEvent(type: String)
}