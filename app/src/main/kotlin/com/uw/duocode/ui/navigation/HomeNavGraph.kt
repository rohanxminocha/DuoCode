package com.uw.duocode.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.uw.duocode.ui.screens.home.HomeView
import com.uw.duocode.ui.screens.lessons.LessonView
import com.uw.duocode.ui.screens.questions.QuestionLoadingView
import com.uw.duocode.ui.screens.questmap.QuestMapView

fun NavGraphBuilder.homeNavGraph(
    navController: NavHostController
){
    navigation<DASHBOARD>(
        startDestination = Home
    ){
        composable<Home>{
            HomeView(navController = navController)
        }

        composable<QuestMap>{
            QuestMapView(navController = navController)
        }

        composable(route = "lessons/{topicId}/{subtopicId}",
        arguments = listOf(
            navArgument("topicId") {
                type = NavType.StringType
                nullable = false
            },
            navArgument("subtopicId") {
                type = NavType.StringType
                nullable = false
            }
        )){ backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
            val subtopicId = backStackEntry.arguments?.getString("subtopicId") ?: ""
            LessonView(
                navController = navController,
                topicId = topicId,
                subtopicId = subtopicId
            )
        }

        composable(route = "questions/{subtopicId}",
            arguments = listOf(
                navArgument("subtopicId") {
                    type = NavType.StringType
                    nullable = false
                }
            )){ backStackEntry ->
            val subtopicId = backStackEntry.arguments?.getString("subtopicId") ?: ""
            QuestionLoadingView(
                navController = navController,
                subtopicId = subtopicId
            )
        }
    }
}
