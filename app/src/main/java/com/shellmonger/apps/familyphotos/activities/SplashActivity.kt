package com.shellmonger.apps.familyphotos.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import kotlin.concurrent.thread

import com.shellmonger.apps.familyphotos.R
import com.shellmonger.apps.familyphotos.lifecycle.ApplicationCrashHandler
import com.shellmonger.apps.familyphotos.lifecycle.ApplicationWrapper
import com.shellmonger.apps.familyphotos.lifecycle.Logger

/**
 * Application entry point - displays a splash screen while loading the
 * application up.
 */
class SplashActivity : AppCompatActivity() {
    private val logger = Logger("SplashActivity")

    /**
     * Activity lifecycle event.  Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     *      down then this Bundle contains the data it most recently supplied in
     *      onSaveInstanceState(Bundle).  Otherwise it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Install the Application Crash Handler
        ApplicationCrashHandler.initialize(this)
    }

    /**
     * Activity lifecycle event.  Called right before a user can interact with the UI.  Note that
     * the UI will freeze until the END of this function!
     */
    override fun onResume() {
        super.onResume()

        thread(start = true) {
            // Create any singletons required

            // Measure startup performance
            val elapsedTime: Long = System.currentTimeMillis() - ApplicationWrapper.startTime
            if (elapsedTime > 3000)
                logger.warning("EXCESSIVE START TIME: $elapsedTime ms")

            // Transition to the next activity
            Thread.sleep(2000)
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
