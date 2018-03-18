package com.shellmonger.apps.familyphotos.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.shellmonger.apps.familyphotos.R;
import com.shellmonger.apps.familyphotos.adapters.PhotoListAdapter;
import com.shellmonger.apps.familyphotos.lifecycle.RequestCodes;
import com.shellmonger.apps.familyphotos.models.Photo;
import com.shellmonger.apps.familyphotos.repositories.RepositoryException;
import com.shellmonger.apps.familyphotos.repositories.RepositoryFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The main entry activity for the application.  This activity assumes that all the
 * singleton setups have already been done, so we don't need to do it separately.
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    /**
     * Reference for the replacement toolbar
     */
    @BindView(R.id.main_toolbar) Toolbar mToolbar;
    /**
     * Reference for the recyclerview component
     */
    @BindView(R.id.list_photos) RecyclerView mPhotoList;

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
        ButterKnife.bind(this);

        // Configure the action bar
        setSupportActionBar(mToolbar);

        // Set up the list
        mPhotoList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mPhotoList.setLayoutManager(layoutManager);

        // Configure a data adapter for the list
        try {
            RecyclerView.Adapter listAdapter = new PhotoListAdapter();
            mPhotoList.setAdapter(listAdapter);
        } catch (RepositoryException err) {
            Log.e(TAG, "RepositoryException while creating list adapter", err);
            throw new RuntimeException("RepositoryException", err);
        }

        // Hide the photo buttons if there is no camera
        PackageManager pm = getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.d(TAG, "Device does not have the camera");

            // Disable the AppBar version of the icon
            ImageButton cameraButton = findViewById(R.id.btn_camera);
            cameraButton.setVisibility(View.INVISIBLE);

            // Disable the FAB
            FloatingActionButton fabCamera = findViewById(R.id.fab_camera);
            fabCamera.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Callback to handle the response from startActivityForResult()
     * @param requestCode the ID of the request causing the response
     * @param resultCode the ID of the response
     * @param data the response data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RequestCodes.TAKE_PICTURE:
                handleCameraPictureTaken(resultCode, data);
                break;
        }
    }

    /**
     * Event handler called when the camera icon is clicked
     * @param v the view that initiated this call
     */
    public void handleOnCameraIconClicked(View v) {
        Log.i(TAG, "handleOnCameraIconClicked");

        PackageManager pm = getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.i(TAG, "Photo requested, but no camera");
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(pm) != null) {
            startActivityForResult(intent, RequestCodes.TAKE_PICTURE);
        } else {
            Log.e(TAG, "No Activity available to handle camera photo");
        }
    }

    /**
     * Event handler called when the camera returns back to the app
     * @param resultCode the code for the response (RESULT_OK is good)
     * @param data the intent with the data block
     */
    private void handleCameraPictureTaken(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            Log.e(TAG, "Camera did not produce data - aborting");
        }
        Bundle extras = data.getExtras();
        Bitmap picture = (Bitmap) extras.get("data");

        // Store the data in the repository
        try {
            Log.i(TAG, "Received image data from the camera");
            Photo newPhoto = new Photo();
            newPhoto.setPicture(picture);
            RepositoryFactory.getPhotosRepository().saveItem(newPhoto);
        } catch (RepositoryException err) {
            Log.e(TAG, "Cannot save new picture to repository", err);
            throw new RuntimeException("RepositoryException", err);
        }
    }
}
