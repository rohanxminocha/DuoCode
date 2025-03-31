package com.uw.duocode.ui.notification

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit


fun scheduleDailyNotificationWork(context: Context) {
    val currentTimeMillis = System.currentTimeMillis()
    val calendar = Calendar.getInstance().apply {
        timeInMillis = currentTimeMillis
        set(Calendar.HOUR_OF_DAY, 18)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        if (timeInMillis < currentTimeMillis) {
            add(Calendar.DAY_OF_MONTH, 1)
        }
    }
    val initialDelay = calendar.timeInMillis - currentTimeMillis

    val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyNotificationWorker>(1, TimeUnit.DAYS)
        .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "daily_notification_work",
        ExistingPeriodicWorkPolicy.REPLACE,
        dailyWorkRequest
    )
}

fun scheduleDynamicNotificationWork(context: Context, delayHours: Long = 6) {
    val workRequest = androidx.work.OneTimeWorkRequestBuilder<DynamicNotificationWorker>()
        .setInitialDelay(delayHours, TimeUnit.HOURS)
        .build()
    WorkManager.getInstance(context).enqueue(workRequest)
}
