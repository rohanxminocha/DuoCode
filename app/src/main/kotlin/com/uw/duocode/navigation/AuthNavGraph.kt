package com.uw.duocode.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.uw.duocode.screens.AuthScreen

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController
){
    navigation<AUTH>(
        startDestination = LoginAndSignup
    ){
        composable<LoginAndSignup>{
            AuthScreen(navController = navController)
        }
    }
}