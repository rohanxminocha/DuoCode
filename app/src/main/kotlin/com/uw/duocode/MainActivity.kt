package com.uw.duocode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.uw.duocode.ui.components.TutorialViewModel
import com.uw.duocode.ui.navigation.SetupNavGraph
import com.uw.duocode.ui.theme.DuocodeTheme
import com.uw.duocode.ui.notification.createNotificationChannel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel(applicationContext, this)

        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            // Create a shared TutorialViewModel instance with factory
            val tutorialViewModel: TutorialViewModel = viewModel(factory = TutorialViewModel.Factory)
            
            DuocodeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SetupNavGraph(
                        navController = navController,
                        tutorialViewModel = tutorialViewModel
                    )
                }
            }
        }
    }
}
