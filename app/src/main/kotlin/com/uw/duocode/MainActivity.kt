package com.uw.duocode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.uw.duocode.ui.navigation.SetupNavGraph
import com.uw.duocode.ui.notification.createNotificationChannel
import com.uw.duocode.ui.notification.scheduleDailyNotificationWork
import com.uw.duocode.ui.theme.DuocodeTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel(applicationContext, this)
        scheduleDailyNotificationWork(applicationContext)
        enableEdgeToEdge()

        setContent {
            DuocodeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    SetupNavGraph(navController = navController)
                }
            }
        }
    }
}
