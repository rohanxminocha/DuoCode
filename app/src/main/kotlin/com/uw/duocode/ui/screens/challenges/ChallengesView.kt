package com.uw.duocode.ui.screens.challenges

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uw.duocode.ui.components.ProgressBar

@Composable
fun ChallengesView() {

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

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(40.dp))

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
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
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