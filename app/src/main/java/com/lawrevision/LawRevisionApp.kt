package com.lawrevision

import android.app.Application
import androidx.work.*
import com.lawrevision.notifications.RevisionNotificationWorker
import java.util.concurrent.TimeUnit

class LawRevisionApp : Application() {
    override fun onCreate() {
        super.onCreate()
        setupDailyNotifications()
    }

    private fun setupDailyNotifications() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val notificationWork = PeriodicWorkRequestBuilder<RevisionNotificationWorker>(
            24, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "daily_notification",
            ExistingPeriodicWorkPolicy.KEEP,
            notificationWork
        )
    }
} 