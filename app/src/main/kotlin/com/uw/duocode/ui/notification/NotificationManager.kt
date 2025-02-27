package com.uw.duocode.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.uw.duocode.MainActivity


private const val CHANNEL_ID = "reminder_channel_id"
private const val REQUEST_CODE_PERMISSION = 123

//fun createNotificationChannel(context: Context) {
//
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        val name = "Reminder Notifications"
//        val descriptionText = "Set timely reminders"
//        val importance = NotificationManager.IMPORTANCE_HIGH
//        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
//            description = descriptionText
//        }
//        val notificationManager: NotificationManager =
//            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.createNotificationChannel(channel)
//    }
//}

fun createNotificationChannel(context: Context, activity: Activity) {

    val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_DENIED) {

                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }

        val name = "Reminder Notifications"
        val descriptionText = "Set timely reminders for DuoCode"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        notificationManager.createNotificationChannel(channel)
    }
}


fun showReminderNotification(context: Context) {

//    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//
//    val triggerTime = System.currentTimeMillis() + 60 * 1000 // 1-minute delay
//    val interval = 60 * 1000 // Repeat every minute
//
//    alarmManager.setRepeating(
//        AlarmManager.RTC_WAKEUP,
//        triggerTime,
//        interval.toLong(),
//        pendingIntent
//    )


    println("here")
    val notificationManager = NotificationManagerCompat.from(context)

    val notificationId = 1
    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        context, 0, intent, PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)

        .setSmallIcon(android.R.drawable.ic_popup_reminder)
        .setContentTitle("DuoCode CheckIn")
        .setContentText("Did you complete today's quest?")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setFullScreenIntent(pendingIntent, true)
        .setChannelId(CHANNEL_ID)
        .setAutoCancel(true)
        .build()
//        .setContentIntent(pendingIntent)

//

    // Show the notification
    notificationManager.notify(1, notification)
}

//fun cancelNotifications() {
//    alarmManager.cancel(pendingIntent)
//}