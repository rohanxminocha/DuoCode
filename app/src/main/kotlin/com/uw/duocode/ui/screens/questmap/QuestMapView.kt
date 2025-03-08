package com.uw.duocode.ui.screens.questmap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.uw.duocode.data.model.SubtopicInfo
import com.uw.duocode.ui.utils.getTopicIcon


@Composable
fun QuestMapView(
    navController: NavHostController,
    viewModel: QuestMapViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.fetchData()
    }

    val topics = viewModel.topics
    val allSubtopics = viewModel.subtopics
    val userProgress = viewModel.userSubtopicsProgress // per-user progress
    val isLoading = viewModel.isLoading
    val error = viewModel.error
    val scrollState = rememberScrollState()
    val currentUser = FirebaseAuth.getInstance().currentUser

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
                val userProgressMap = userProgress.associateBy { it.id }

                val sortedTopics = topics.sortedBy { it.order }
                sortedTopics.forEachIndexed { topicIndex, topic ->
                    val subtopicInfos: List<SubtopicInfo> = allSubtopics
                        .filter { it.topicId == topic.id }
                        .sortedBy { it.order }

                    TopicCard(
                        title = topic.name,
                        icon = getTopicIcon(topic.iconKey),
                        onButtonClick = {
                            subtopicInfos.firstOrNull()?.let { firstSubtopic ->
                                navController.navigate("lessons/${topic.id}/${firstSubtopic.id}")
                            }
                        }
                    )

                    val isTopicUnlocked = if (topicIndex == 0) {
                        true
                    } else {
                        val prevTopic = sortedTopics[topicIndex - 1]
                        val prevSubs = allSubtopics.filter { it.topicId == prevTopic.id }

                        prevSubs.isNotEmpty() && prevSubs.all { s ->
                            userProgressMap[s.id]?.completed == true
                        }
                    }

                    subtopicInfos.forEachIndexed { subIndex, subtopic ->
                        val isFirstSubtopic = (subIndex == 0)
                        val showActionButton = if (isFirstSubtopic) {
                            isTopicUnlocked
                        } else {
                            val prevSub = subtopicInfos[subIndex - 1]
                            userProgressMap[prevSub.id]?.completed == true
                        }

                        val isSubtopicCompleted = (userProgressMap[subtopic.id]?.completed == true)
                        val buttonText = if (isSubtopicCompleted) "Review" else "Start"

                        LessonItem(
                            title = subtopic.name,
                            icon = Icons.Default.Code,
                            buttonText = buttonText,
                            showActionButton = showActionButton,
                            onButtonClick = {
                                if (showActionButton) {
                                    navController.navigate("questions/${subtopic.id}")
                                }
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
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        tonalElevation = 10.dp
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
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onButtonClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
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
                    containerColor =
                    if (buttonText == "Review") MaterialTheme.colorScheme.secondaryContainer
                    else MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (buttonText == "Start") {
                    Spacer(modifier = Modifier.width(4.dp))
                }
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
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}
