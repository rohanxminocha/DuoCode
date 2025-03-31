package com.uw.duocode.ui.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.uw.duocode.MainActivity
import com.uw.duocode.R
import com.uw.duocode.data.model.Notification
import kotlinx.coroutines.tasks.await
import kotlin.random.Random


class DynamicNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val CHANNEL_ID = "reminder_channel_id"

    override suspend fun doWork(): Result {
        return try {
            val templatesSnapshot = FirebaseFirestore.getInstance()
                .collection("notifications")
                .get()
                .await()
            val templates =
                templatesSnapshot.documents.mapNotNull { it.toObject(Notification::class.java) }
            if (templates.isEmpty()) return Result.failure()

            // Pick a random notification template
            val randomTemplate = templates.random()

            val intent = Intent(applicationContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.mipmap.duocode_round)
                .setContentTitle(randomTemplate.title)
                .setContentText(randomTemplate.message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(applicationContext)
                .notify(Random.nextInt(), notification)
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
