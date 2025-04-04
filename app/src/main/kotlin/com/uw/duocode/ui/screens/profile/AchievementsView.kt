package com.uw.duocode.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.uw.duocode.ui.utils.getTopicIcon

@Composable
fun AchievementsView(
    achievementsViewModel: AchievementsViewModel
) {
    val achievements = achievementsViewModel.achievements
    val isLoading = achievementsViewModel.isLoading
    val error = achievementsViewModel.error

    LaunchedEffect(Unit) {
        achievementsViewModel.loadAchievements()
    }

    val unlockedAchievements = achievements.filter { it.unlocked }
    val lockedAchievements = achievements.filter { !it.unlocked }

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("Unlocked", "Locked")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Achievements",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                return@Column
            }
            error != null -> {
                Text(text = error ?: "Unknown error", color = Color.Red)
                return@Column
            }
            achievements.isEmpty() -> {
                Text(text = "No achievements yet. Start completing questions!")
                return@Column
            }
        }

        TabRow(selectedTabIndex = selectedTabIndex) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTabIndex) {
            0 -> AchievementGrid(achievements = unlockedAchievements)
            1 -> AchievementGrid(achievements = lockedAchievements)
        }
    }
}

@Composable
fun AchievementGrid(achievements: List<AchievementData>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 0.dp, max = 400.dp)
    ) {
        items(achievements.size) { index ->
            AchievementItem(achievement = achievements[index])
        }
    }
}

@Composable
fun AchievementItem(achievement: AchievementData) {
    val medalColor = if (achievement.unlocked) MaterialTheme.colorScheme.primary else Color.Gray

    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(160.dp)
            .height(170.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
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
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

