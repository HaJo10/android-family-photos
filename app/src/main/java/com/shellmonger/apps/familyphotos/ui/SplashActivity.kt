package com.shellmonger.apps.familyphotos.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.shellmonger.apps.familyphotos.R
import com.shellmonger.apps.familyphotos.lifecycle.ApplicationWrapper
import com.shellmonger.apps.familyphotos.services.interfaces.AnalyticsService
import org.koin.android.ext.android.inject
import kotlin.concurrent.thread

/**
 * Entry-point Activity for the application.  This activity is responsible for
 * a) Displaying the splash screen
 * b) Doing any long-winded startup tasks
 * c) Recording the boot-up time into analytics
 * d) Transitioning to the main activity
 */
class SplashActivity : AppCompatActivity() {
    companion object {
        private val TAG: String = this::class.java.simpleName
    }

    /**
     * Analytics service (provided via dependency injection)
     */
    private val analyticsService: AnalyticsService by inject()

    /**
     * Called when the activity is starting. This is where most initialization should go: calling
     * setContentView(int) to inflate the activity's UI, initializing any view models, etc.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")
        setContentView(R.layout.activity_splash)


        // Record the analyticsService start
        analyticsService.startSession()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume()")

        thread(start = true) {
            // Initialize any repositories or other singletons

            // Record the start time
            val elapsedTime = System.currentTimeMillis() - ApplicationWrapper.STARTUP_TIME
            analyticsService.recordEvent("app_startup", null, mapOf("boot" to elapsedTime.toDouble()))
            if (elapsedTime > 3000) analyticsService.recordEvent("long_app_startup")

            // Start the next activity
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        }
    }
}
