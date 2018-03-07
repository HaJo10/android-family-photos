package com.shellmonger.apps.familyphotos.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.shellmonger.apps.familyphotos.R;
import com.shellmonger.apps.familyphotos.lifecycle.ApplicationWrapper;

/**
 * Splash Screen - first screen within the app, responsible for creating references
 * to the singletons (if any), recording start-up time and setting up any progress
 * type spinners to show activity.
 */
public class SplashActivity extends AppCompatActivity {
    /**
     * The tag to be used for logging
     */
    private String TAG = this.getClass().getSimpleName();

    /**
     * Called when the activity is starting to initialize the activity.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                           being shut down, then this bundle contains the data most
     *                           recently supplied in onSaveInstanceState(Bundle).  We don't
     *                           use this, so it will always be null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Record the start-up time
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - ApplicationWrapper.startTime;
        if (elapsedTime > 3000) {
            Log.e(TAG, "LONG STARTUP TIME");
        }
        Log.d(TAG, String.format("Elapsed Time = %d ms", elapsedTime));

        // Transition to the MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
