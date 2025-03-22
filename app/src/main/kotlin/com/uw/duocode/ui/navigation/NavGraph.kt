package com.uw.duocode.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.google.firebase.auth.FirebaseAuth
import com.uw.duocode.ui.components.TutorialViewModel

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    tutorialViewModel: TutorialViewModel
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    NavHost(
        navController = navController,
        startDestination = if (currentUser != null) DASHBOARD else AUTH
    ) {
        homeNavGraph(
            navController = navController,
            tutorialViewModel = tutorialViewModel
        )
        authNavGraph(
            navController = navController,
            tutorialViewModel = tutorialViewModel
        )
    }
}
