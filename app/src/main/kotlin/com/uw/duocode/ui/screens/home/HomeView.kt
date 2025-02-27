package com.uw.duocode.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.uw.duocode.ui.screens.profile.AchievementsView
import com.uw.duocode.ui.screens.profile.ProfileView
import com.uw.duocode.ui.screens.questmap.QuestMapView

@Composable
fun HomeView(
    navController: NavHostController,
    viewModel: HomeViewModel = viewModel()
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                viewModel.tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        icon = { Icon(viewModel.icons[index], contentDescription = title) },
                        label = { Text(title) },
                        selected = viewModel.selectedTab == index,
                        onClick = { viewModel.onTabSelected(index) }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (viewModel.selectedTab) {
                0 -> QuestMapView(navController)
                1 -> AchievementsView()
                2 -> ProfileView(navController)
            }
        }
    }
}
