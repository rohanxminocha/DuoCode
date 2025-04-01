package com.uw.duocode.ui.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.uw.duocode.MainActivity
import com.uw.duocode.R
import com.uw.duocode.data.model.Notification
import kotlinx.coroutines.tasks.await


class DailyNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val CHANNEL_ID = "reminder_channel_id"

    override suspend fun doWork(): Result {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return Result.failure()
        val userId = currentUser.uid
        val db = FirebaseFirestore.getInstance()
        try {
            val userDoc = db.collection("users").document(userId).get().await()
            val notificationsEnabled = userDoc.getBoolean("notificationEnabled") ?: true
            if (!notificationsEnabled) return Result.success()

            val notificationsSentToday = (userDoc.getLong("notificationsSentToday") ?: 0L).toInt()
            if (notificationsSentToday >= 3) return Result.success()

            val templatesSnapshot = db.collection("notifications").get().await()
            val templates = templatesSnapshot.documents.mapNotNull { it.toObject(Notification::class.java) }
            if (templates.isEmpty()) return Result.failure()

            val dailyReminder = templates.firstOrNull { it.type == "dailyReminder" }
            dailyReminder?.let {
                sendNotification(it)
                db.collection("users").document(userId)
                    .update("notificationsSentToday", FieldValue.increment(1))
                    .await()
            }

            val streakNotifiedToday = userDoc.getBoolean("streakNotifiedToday") ?: false
            if (!streakNotifiedToday) {
                val streakAlert = templates.firstOrNull { it.type == "streakAlert" }
                streakAlert?.let {
                    val currentStreak = (userDoc.getLong("currentStreak") ?: 0L).toInt()
                    val customizedMessage = it.message.replace("{streak}", currentStreak.toString())
                    val customizedNotif = it.copy(message = customizedMessage)
                    sendNotification(customizedNotif)
                    db.collection("users").document(userId)
                        .update(
                            "notificationsSentToday", FieldValue.increment(1),
                            "streakNotifiedToday", true
                        ).await()
                }
            }
            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }
    }

    private fun sendNotification(notification: Notification) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.duocode_notif)
            .setContentTitle(notification.title)
            .setContentText(notification.message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(applicationContext)) {
            notify(notification.id.hashCode(), builder.build())
        }
    }
}
