package com.uw.duocode.ui.screens.questmap

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uw.duocode.ui.utils.getTopicIcon


@Composable
fun QuestMapView(
    navController: NavHostController,
    viewModel: QuestMapViewModel = viewModel()
) {
    val topics = viewModel.topics
    val isLoading = viewModel.isLoading
    val error = viewModel.error

    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Welcome back, ${currentUser?.displayName}! \uD83D\uDC4B",
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(20.dp))

        when {
            isLoading -> {
                Text("Loading...")
            }

            error != null -> {
                Text("Error: $error")
            }

            else -> {
                val sortedTopics = topics.sortedBy { it.order }

                sortedTopics.forEach { topic ->
                    val sortedSubtopics = topic.subtopics.sortedBy { it.order }

                    TopicCard(
                        title = topic.name,
                        icon = getTopicIcon(topic.iconKey),
                        onButtonClick = {
                            navController.navigate("lessons/${topic.id}/${sortedSubtopics.firstOrNull()?.id}")
                        }
                    )

                    sortedSubtopics.forEachIndexed { index, subtopic ->
                        val showActionButton = if (index == 0) {
                            true
                        } else {
                            sortedSubtopics[index - 1].isCompleted
                        }

                        LessonItem(
                            title = subtopic.name,
                            icon = Icons.Default.Code,
                            showActionButton = showActionButton,
                            onButtonClick = {
                                navController.navigate("questions/${subtopic.id}")
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(15.dp))
                }
            }
        }
    }
}

@Composable
fun TopicCard(
    title: String,
    buttonText: String = "Learn",
    icon: ImageVector? = null,
    onButtonClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF1EAFE)
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.Gray),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                IconContainer(icon = icon)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onButtonClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7F5CE5),
                    contentColor = Color.White
                )
            ) {
                Text(text = buttonText)
            }
        }
    }
}

@Composable
fun LessonItem(
    title: String,
    icon: ImageVector,
    buttonText: String = "Start",
    showActionButton: Boolean = false,
    onButtonClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, top = 4.dp, bottom = 4.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconContainer(icon = icon)

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
            modifier = Modifier.weight(1f)
        )

        if (showActionButton) {
            Button(
                onClick = onButtonClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7F5CE5),
                    contentColor = Color.White
                )
            ) {
                Text(text = buttonText)
            }
        }
    }
}

@Composable
fun IconContainer(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color(0xFFD7C3F2)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF7F5CE5)
        )
    }
}
