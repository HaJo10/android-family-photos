package com.shellmonger.apps.familyphotos.services

import android.content.Context
import android.util.Log
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager

class AWSAnalyticsService(context: Context) : AnalyticsService {
    private val TAG = this::class.java.simpleName
    private val pinpointManager: PinpointManager

    override fun startSession() {
        with (pinpointManager) {
            sessionClient.startSession()
            analyticsClient.submitEvents()
        }
    }

    override fun stopSession() {
        with (pinpointManager) {
            sessionClient.stopSession()
            analyticsClient.submitEvents()
        }
    }

    override fun recordEvent(type: String) {
        with (pinpointManager.analyticsClient) {
            val event = createEvent(type)
            recordEvent(event)
            submitEvents()
        }
    }

    init {
        Log.d(TAG, "constructor - context = ${context}")
        val awsConfiguration = AWSConfiguration(context)
        val credentialsProvider = CognitoCachingCredentialsProvider(context, awsConfiguration)
        val pinpointConfiguration = PinpointConfiguration(context, credentialsProvider, awsConfiguration)
        pinpointManager = PinpointManager(pinpointConfiguration)
    }
}