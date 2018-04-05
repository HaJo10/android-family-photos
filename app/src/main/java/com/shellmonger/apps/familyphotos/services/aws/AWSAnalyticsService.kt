package com.shellmonger.apps.familyphotos.services.aws

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.util.Log
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager
import com.shellmonger.apps.familyphotos.extensions.getConnectivityManager
import com.shellmonger.apps.familyphotos.services.interfaces.AnalyticsService

class AWSAnalyticsService(context: Context) : AnalyticsService {
    /**
     * Reference to the AWS PinpointManager object
     */
    private var manager: PinpointManager? = null

    /**
     * true if the app is connected to the internet
     */
    private var isConnected = false

    companion object {
        val TAG: String = this::class.java.simpleName
    }

    init {
        Log.d(TAG, "Initializing AWS Analytics Service")

        // Load the configuration file
        val awsConfig = AWSConfiguration(context)

        // Get the credentials provider for submitting events
        val cred = CognitoCachingCredentialsProvider(context, awsConfig)

        // Create the AWS PinpointManager that will be used elsewhere in the class
        manager = PinpointManager(PinpointConfiguration(context, cred, awsConfig))

        // Initialize a network state receiver
        val networkStateReceiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                isConnected = ctx?.getConnectivityManager()?.activeNetworkInfo != null
                if (isConnected) manager?.analyticsClient?.submitEvents()
            }
        }
        context.registerReceiver(networkStateReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        // Determine the current connectivity state
        isConnected = context.getConnectivityManager().activeNetworkInfo != null
    }

    /**
     * Records a start session event into the analytics stream
     */
    override fun startSession() {
        manager?.let {
            it.sessionClient.startSession()
            it.analyticsClient.submitEvents()
        }
    }

    /**
     * Records a stop session event into the analytics stream
     */
    override fun stopSession() {
        manager?.let {
            it.sessionClient.startSession()
            it.analyticsClient.submitEvents()
        }
    }

    /**
     * Record a custom event into the analytics stream
     *
     * @param name the custom event name
     * @param attributes a list of key-value pairs for recording string attributes
     * @param metrics a list of key-value pairs for recording numeric metrics
     */
    override fun recordEvent(name: String, attributes: Map<String, String>?, metrics: Map<String, Double>?) {
        manager?.let {
            val event = it.analyticsClient.createEvent(name)
            attributes?.let {
                for ((k, v) in it) {
                    event.addAttribute(k, v)
                }
            }
            metrics?.let {
                for ((k, v) in it) {
                    event.addMetric(k, v)
                }
            }
            it.analyticsClient.recordEvent(event)
            it.analyticsClient.submitEvents()
        }
    }
}