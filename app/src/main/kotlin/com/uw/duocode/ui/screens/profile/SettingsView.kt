package com.uw.duocode.ui.screens.profile

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.Switch
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.uw.duocode.R
import com.uw.duocode.ui.navigation.AUTH
import com.uw.duocode.ui.notification.NotificationReceiver
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(
    navController: NavHostController
) {
    val context = LocalContext.current
    val sharedPreferences =
        context.getSharedPreferences("notification_settings", Context.MODE_PRIVATE)
    var notificationEnabled by remember {
        mutableStateOf(sharedPreferences.getBoolean("enabled_notifications", false))
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Reminders",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Enable Reminders", style = MaterialTheme.typography.bodyLarge)

                        Switch(
                            checked = notificationEnabled,
                            onCheckedChange = { isChecked ->
                                notificationEnabled = isChecked
                                if (isChecked) {
                                    if (ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.POST_NOTIFICATIONS
                                        ) != PackageManager.PERMISSION_GRANTED
                                    ) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            ActivityCompat.requestPermissions(
                                                context as Activity,
                                                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                                                1
                                            )
                                        }
                                    } else {
                                        sharedPreferences.edit()
                                            .putBoolean("enabled_notifications", true).apply()
                                        scheduleDailyNotification(context)
                                    }
                                } else {
                                    sharedPreferences.edit()
                                        .putBoolean("enabled_notifications", false).apply()
                                    cancelNotification(context)
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = { showTestNotification(context) }) {
                        Text("Test Notification")
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Account",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            FirebaseAuth.getInstance().signOut()
                            navController.navigate(AUTH)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Sign Out")
                    }
                }
            }
        }
    }
}

private fun scheduleDailyNotification(context: Context) {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 18)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }
    val intent = android.content.Intent(context, NotificationReceiver::class.java)
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
    val intent = android.content.Intent(context, NotificationReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(pendingIntent)
}

private fun showTestNotification(context: Context) {
    val intent = android.content.Intent(context, com.uw.duocode.MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        context, 0, intent, PendingIntent.FLAG_IMMUTABLE
    )

    val notification = androidx.core.app.NotificationCompat.Builder(context, "reminder_channel_id")
        .setSmallIcon(R.drawable.duonotif)
        .setContentTitle("DuoCode Test")
        .setContentText("This is a test notification.")
        .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
        .setChannelId("reminder_channel_id")
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }
    }
    NotificationManagerCompat.from(context).notify(1, notification.build())
}
