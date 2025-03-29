package com.uw.duocode.ui.notification

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build


private const val CHANNEL_ID = "reminder_channel_id"

fun createNotificationChannel(context: Context, activity: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Reminder Notifications"
        val descriptionText = "Timely DuoCode reminders"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
