package com.shellmonger.apps.familyphotos.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.shellmonger.apps.familyphotos.R;

/**
 * The main entry activity for the application.  This activity assumes that all the
 * singleton setups have already been done, so we don't need to do it separately.
 */
public class MainActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_main);
    }
}
