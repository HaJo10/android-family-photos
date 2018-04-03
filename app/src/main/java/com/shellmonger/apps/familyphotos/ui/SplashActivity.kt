package com.shellmonger.apps.familyphotos.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.shellmonger.apps.familyphotos.R
import com.shellmonger.apps.familyphotos.services.AnalyticsService
import org.koin.android.ext.android.inject

class SplashActivity : AppCompatActivity() {
    val analyticsService: AnalyticsService by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        analyticsService.startSession()
    }
}
