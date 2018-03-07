package com.shellmonger.apps.familyphotos.lifecycle;

import android.app.Application;

/**
 * Class for maintaining global application state.  This class is instantiated
 * before any other class when the process for your application is created.
 */
public class ApplicationWrapper extends Application {
    public static long startTime = System.currentTimeMillis();

    /**
     * Called when the application is starting, before any activity, service, or
     * receiver objects (excluding content providers) have been created.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityLifecycle());
    }

    /**
     * Called when the overall system is running low on memory, and actively running
     * processes should trim their memory usage.
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
