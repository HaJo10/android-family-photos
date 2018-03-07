package com.shellmonger.apps.familyphotos.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.shellmonger.apps.familyphotos.R;
import com.shellmonger.apps.familyphotos.lifecycle.ApplicationWrapper;

public class SplashActivity extends AppCompatActivity {
    private String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

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
