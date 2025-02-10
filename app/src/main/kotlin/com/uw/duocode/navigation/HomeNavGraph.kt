package com.uw.duocode.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.uw.duocode.screens.MainScreen

fun NavGraphBuilder.homeNavGraph(
    navController: NavHostController
){
    navigation<DASHBOARD>(
        startDestination = Home
    ){
        composable<Home>{
            MainScreen(navController = navController)
        }
    }
}