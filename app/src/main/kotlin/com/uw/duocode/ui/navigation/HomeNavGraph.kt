package com.uw.duocode.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.uw.duocode.ui.screens.home.HomeView
import com.uw.duocode.ui.screens.lessons.LessonView
import com.uw.duocode.ui.screens.questmap.QuestMapView
import com.uw.duocode.ui.screens.questions.MatchView
import com.uw.duocode.ui.screens.questions.MultipleChoiceView

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

        composable<LessonArrays>{
            LessonView(navController = navController)
        }

        composable<Match>{
            MatchView(navController = navController)
        }

        composable<MultipleChoice>{
            MultipleChoiceView(navController = navController)
        }
    }
}
