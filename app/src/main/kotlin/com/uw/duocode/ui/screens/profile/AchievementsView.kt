package com.uw.duocode.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.uw.duocode.ui.utils.getTopicIcon

@Composable
fun AchievementsView(achievementsViewModel: AchievementsViewModel) {
    val achievements = achievementsViewModel.achievements
    val isLoading = achievementsViewModel.isLoading
    val error = achievementsViewModel.error

    LaunchedEffect(Unit) {
        achievementsViewModel.loadAchievements()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Achievements",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            error != null -> {
                Text(text = error ?: "Unknown error", color = Color.Red)
            }
            achievements.isEmpty() -> {
                Text(text = "No achievements yet. Start completing challenges!")
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier.fillMaxWidth().height(300.dp)
                ) {
                    items(achievements.size) { index ->
                        AchievementItem(achievement = achievements[index])
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementItem(achievement: AchievementData) {
    val medalColor = if (achievement.unlocked) Color(0xFFFFD700) else Color.Gray // Gold vs Grey

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(64.dp)
                .background(medalColor, shape = CircleShape)
        ) {
            Icon(
                imageVector = getTopicIcon(achievement.iconName),
                contentDescription = achievement.title,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = achievement.title,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

