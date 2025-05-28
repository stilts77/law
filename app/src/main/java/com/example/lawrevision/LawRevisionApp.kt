package com.example.lawrevision

import android.app.Application
import androidx.work.Configuration

class LawRevisionApp : Application(), Configuration.Provider {
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
    }
} 