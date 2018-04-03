package com.shellmonger.apps.familyphotos.lifecycle

import android.app.Application
import com.shellmonger.apps.familyphotos.services.AWSAnalyticsService
import com.shellmonger.apps.familyphotos.services.AnalyticsService
import org.koin.android.ext.android.startKoin
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

val appModule : Module = applicationContext {
    bean { AWSAnalyticsService(get()) as AnalyticsService }
}

class ApplicationWrapper : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Koin dependency injection
        startKoin(this, listOf(appModule))
    }
}