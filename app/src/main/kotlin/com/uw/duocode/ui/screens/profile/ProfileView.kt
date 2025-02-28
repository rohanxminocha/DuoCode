package com.uw.duocode.ui.screens.profile

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.uw.duocode.MainActivity
import com.uw.duocode.ui.navigation.AUTH
import com.uw.duocode.ui.notification.NotificationReceiver
import java.util.Calendar

@Composable
fun ProfileView(navController: NavHostController) {
    val context = LocalContext.current

    val sharedPreferences = context.getSharedPreferences("notification_settings", Context.MODE_PRIVATE)

    val enabledNotifications = remember { mutableStateOf(
        sharedPreferences.getBoolean("enabled_notifications", false)
    ) }

    val notificationEnabled = remember { mutableStateOf(enabledNotifications.value) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium
        )

        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        currentUser?.let { user ->
            user.displayName?.let { name ->
                Text("Name: $name")
            }
            Text("Email: ${user.email}")
        }
        Column (horizontalAlignment = Alignment.CenterHorizontally){
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Enable Reminders?")
                Spacer(modifier = Modifier.width(8.dp))
                LaunchedEffect(enabledNotifications.value) {
                    if (enabledNotifications.value) {
                        scheduleDailyNotification(context)
                    } else {
                        cancelNotification(context)
                    }
                }

                Switch(
                    checked = notificationEnabled.value,
                    onCheckedChange = { isChecked ->
                        notificationEnabled.value = isChecked
                        if (isChecked) {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(
                                    context as Activity,
                                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                                    1
                                )
                            } else {
                                enabledNotifications.value = true
                                sharedPreferences.edit().putBoolean("enabled_notifications", true).apply()
                            }
                            scheduleDailyNotification(context)
                        } else {
                            enabledNotifications.value = false
                            sharedPreferences.edit().putBoolean("enabled_notifications", false).apply()
                            cancelNotification(context)
                        }
                    },
                )
            }

            Text(
                text = "Test Notification",
                color = Color(0xFF6A4CAF),
                modifier = Modifier
                    .clickable { showTestNotification(context) }
            )
        }


        Button(
            onClick = {
                auth.signOut()
                navController.navigate(
                    AUTH
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6A4CAF),
                contentColor = Color.White
            ),
        ) {
            Text("Sign Out")
        }
    }
}

private fun scheduleDailyNotification(context: Context) {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 18)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }

    val intent = Intent(context, NotificationReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setRepeating(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        AlarmManager.INTERVAL_DAY,
        pendingIntent
    )
}

private fun cancelNotification(context: Context) {
    val intent = Intent(context, NotificationReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(pendingIntent)
}

fun showTestNotification(context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        context, 0, intent, PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, "reminder_channel_id")
        .setSmallIcon(android.R.drawable.ic_popup_reminder)
        .setContentTitle("DuoCode Test")
        .setContentText("This is a test notification.")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setChannelId("reminder_channel_id")
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    NotificationManagerCompat.from(context).notify(1, notification.build())
}