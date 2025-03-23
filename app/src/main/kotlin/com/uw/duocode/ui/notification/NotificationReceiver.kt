package com.uw.duocode.ui.notification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.uw.duocode.MainActivity
import com.uw.duocode.R

private const val CHANNEL_ID = "reminder_channel_id"

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        showReminderNotification(context)
    }

    fun showReminderNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.duonotif)
            .setContentTitle("DuoCode CheckIn")
            .setContentText("Did you complete today's quest?")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setChannelId(CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(1, notification.build())
    }
}