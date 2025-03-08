package com.uw.duocode.ui.screens.challenges

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.uw.duocode.ui.components.ProgressBar

@Composable
fun ChallengesView(
    leaderboardViewModel: LeaderboardViewModel = viewModel()
) {
    var showLeaderboardDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        leaderboardViewModel.loadLeaderboards()
    }

    //hard coded data, will be made mutable state / handled by viewModel in next sprint
    val currentMonth = "March"
    val progress = 6f/31f
    val dummyChallenges = listOf(
        ChallengeData(
            title = "Arrays & Hashing Beginner",
            subTitle = "Complete the Arrays and Hashing lesson",
            buttonText = "Start"
        ),
        ChallengeData(
            title = "Arrays & Hashing Expert",
            subTitle = "Finish 10 Arrays and Hashing questions",
            buttonText = "Start"
        ),
        ChallengeData(
            title = "Linked Lists Beginner",
            subTitle = "Complete the Linked Lists lesson",
            buttonText = "Start"
        ),
        ChallengeData(
            title = "Linked Lists Intermediate",
            subTitle = "Finish 5 Linked Lists questions",
            buttonText = "Start"
        ),
        ChallengeData(
            title = "Linked Lists Expert",
            subTitle = "Finish 10 Linked Lists questions",
            buttonText = "Start"
        ),
        ChallengeData(
            title = "Binary Search Intermediate",
            subTitle = "Finish 5 Binary Search questions",
            buttonText = "Start"
        ),
        ChallengeData(
            title = "Binary Search Expert",
            subTitle = "Finish 10 Binary Search questions",
            buttonText = "Start"
        )
    )

    if (showLeaderboardDialog) {
        Dialog(
            onDismissRequest = { showLeaderboardDialog = false }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                LeaderboardView(leaderboardViewModel)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Leaderboard",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Leaderboard",
                        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    )
                }
                
                TextButton(
                    onClick = { showLeaderboardDialog = true }
                ) {
                    Text("View All")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "View all",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    if (leaderboardViewModel.isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Loading leaderboard...")
                        }
                    } else if (leaderboardViewModel.globalLeaderboard.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No leaderboard data available")
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Rank",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.width(40.dp),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "User",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Questions",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.width(70.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                        
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        leaderboardViewModel.globalLeaderboard.take(3).forEachIndexed { index, entry ->
                            val rank = index + 1
                            val borderColor = when (rank) {
                                1 -> Color(0xFFFFD700) // Gold
                                2 -> Color(0xFFC0C0C0) // Silver
                                3 -> Color(0xFFCD7F32) // Bronze
                                else -> MaterialTheme.colorScheme.outline
                            }
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(borderColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = rank.toString(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (entry.profilePictureUrl != null) {
                                        SubcomposeAsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(entry.profilePictureUrl)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = "Profile picture",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop,
                                            error = {
                                                Text(
                                                    text = getInitials(entry.name),
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        )
                                    } else {
                                        Text(
                                            text = getInitials(entry.name),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = entry.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (entry.isCurrentUser) FontWeight.Bold else FontWeight.Normal,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                
                                Text(
                                    text = entry.questionsCompletedToday.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(70.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                            
                            if (index < 2) {
                                Divider(
                                    modifier = Modifier
                                        .padding(vertical = 4.dp)
                                        .alpha(0.5f)
                                )
                            }
                        }
                        
                        leaderboardViewModel.currentUserRank?.let { rank ->
                            if (rank > 3) {
                                val currentUserEntry = leaderboardViewModel.globalLeaderboard.find { it.isCurrentUser }
                                if (currentUserEntry != null) {
                                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                                    
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                                            .padding(vertical = 4.dp, horizontal = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = rank.toString(),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.width(40.dp),
                                            textAlign = TextAlign.Center
                                        )
                                        
                                        Box(
                                            modifier = Modifier
                                                .size(30.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (currentUserEntry.profilePictureUrl != null) {
                                                SubcomposeAsyncImage(
                                                    model = ImageRequest.Builder(LocalContext.current)
                                                        .data(currentUserEntry.profilePictureUrl)
                                                        .crossfade(true)
                                                        .build(),
                                                    contentDescription = "Your profile picture",
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentScale = ContentScale.Crop,
                                                    error = {
                                                        Text(
                                                            text = getInitials(currentUserEntry.name),
                                                            color = Color.White,
                                                            fontWeight = FontWeight.Bold,
                                                            style = MaterialTheme.typography.bodySmall
                                                        )
                                                    }
                                                )
                                            } else {
                                                Text(
                                                    text = getInitials(currentUserEntry.name),
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }
                                        
                                        Spacer(modifier = Modifier.width(8.dp))
                                        
                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = "You",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        
                                        Text(
                                            text = currentUserEntry.questionsCompletedToday.toString(),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.width(70.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Daily Challenges",
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = currentMonth,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                ProgressBar(
                    progress = progress,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "6/31 days",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(600.dp) // Fixed height for the LazyColumn
        ) {
            items(dummyChallenges){ challenge ->
                ChallengeItem(
                    title = challenge.title,
                    subTitle = challenge.subTitle,
                    buttonText = challenge.buttonText,
                    onButtonClick = {},
                    icon = Icons.Default.Quiz
                )
            }
        }
    }
}

@Composable
fun ChallengeItem(
    title: String,
    subTitle: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    icon: ImageVector
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        tonalElevation = 10.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = subTitle,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Button(
                onClick = onButtonClick,
                shape = RoundedCornerShape(50)
            ) {
                Text(buttonText)
            }
        }
    }
}

//Move to viewModel when implemented
data class ChallengeData(
    val title: String,
    val subTitle: String,
    val buttonText: String
)

private fun getInitials(name: String): String {
    return if (name.contains("@")) {
        name.split("@")[0].take(1).uppercase()
    } else {
        val parts = name.split(" ")
        when {
            parts.isEmpty() -> "?"
            parts.size == 1 -> parts[0].take(1).uppercase()
            else -> "${parts[0].take(1)}${parts[1].take(1)}".uppercase()
        }
    }
}

private fun Modifier.alpha(alpha: Float): Modifier {
    return this.then(Modifier.background(Color.Gray.copy(alpha = alpha)))
}