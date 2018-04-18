package com.shellmonger.apps.familyphotos.lifecycle

import android.app.Application
import com.shellmonger.apps.familyphotos.services.aws.AWSAnalyticsService
import com.shellmonger.apps.familyphotos.services.aws.AWSIdentityManager
import com.shellmonger.apps.familyphotos.services.interfaces.AnalyticsService
import com.shellmonger.apps.familyphotos.services.interfaces.IdentityManager
import com.shellmonger.apps.familyphotos.services.mock.MockIdentityManager
import com.shellmonger.apps.familyphotos.ui.MainActivityViewModel
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.android.startKoin
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

/**
 * List of dependency injected modules
 */
val appModule : Module = applicationContext {
    bean { AWSAnalyticsService(get()) as AnalyticsService }
    bean { AWSIdentityManager(get()) as IdentityManager }

    viewModel { MainActivityViewModel(get()) }
}

/**
 * The Application class in Android is the base class within an Android app that contains all
 * other components such as activities and services. The Application class, or any subclass of
 * the Application class, is instantiated before any other class when the process for your
 * application/package is created.
 */
class ApplicationWrapper : Application() {
    companion object {
        val STARTUP_TIME = System.currentTimeMillis()
    }

    /**
     * Called when the application is starting, before any activity, service, or receiver objects
     * (excluding content providers) have been created.

     * Implementations should be as quick as possible (for example using lazy initialization of
     * state) since the time spent in this function directly impacts the performance of starting
     * the first activity, service, or receiver in a process.
     */
    override fun onCreate() {
        super.onCreate()

        // Initialize Koin dependency injection
        startKoin(this, listOf(appModule))
    }
}