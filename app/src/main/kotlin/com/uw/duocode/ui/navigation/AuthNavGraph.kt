package com.uw.duocode.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.uw.duocode.ui.screens.auth.AuthView

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController
){
    navigation<AUTH>(
        startDestination = LoginAndSignup
    ){
        composable<LoginAndSignup>{
            AuthView(navController = navController)
        }
    }
}
