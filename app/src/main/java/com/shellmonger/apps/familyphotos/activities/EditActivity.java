package com.shellmonger.apps.familyphotos.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.shellmonger.apps.familyphotos.R;

public class EditActivity extends AppCompatActivity {
    private static final String TAG = "EditActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
    }

    /**
     * Event handler called when the back button is clicked
     * @param v the view that initiated this call
     */
    public void handleOnBackClicked(View v) {
        Log.i(TAG, "handleOnBackClicked");
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
