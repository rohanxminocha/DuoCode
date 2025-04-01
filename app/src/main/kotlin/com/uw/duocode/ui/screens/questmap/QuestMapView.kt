package com.uw.duocode.ui.screens.questmap

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.uw.duocode.data.model.SubtopicInfo
import com.uw.duocode.ui.components.TutorialCarousel
import com.uw.duocode.ui.components.TutorialViewModel
import com.uw.duocode.ui.utils.getTopicIcon


@Composable
fun QuestMapView(
    navController: NavHostController,
    viewModel: QuestMapViewModel = viewModel(),
    tutorialViewModel: TutorialViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.fetchData()
    }

    val currentUser = FirebaseAuth.getInstance().currentUser
    if (currentUser != null) {
        LaunchedEffect(currentUser.uid) {
            viewModel.checkForNewQuestions(currentUser.uid)
        }
    }

    val topics = viewModel.topics
    val allSubtopics = viewModel.subtopics
    val userProgress = viewModel.userSubtopicsProgress // per-user progress
    val isLoading = viewModel.isLoading
    val error = viewModel.error
    val scrollState = rememberScrollState()

    if (tutorialViewModel.showTutorial) {
        TutorialCarousel(
            slides = tutorialViewModel.tutorialSlides,
            onDismiss = {
                tutorialViewModel.dismissTutorial()
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        FloatingActionButton(
            onClick = { tutorialViewModel.showTutorial() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp)
                .size(56.dp)
                .zIndex(10f),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp
            )
        ) {
            Icon(
                imageVector = Icons.Default.Help,
                contentDescription = "Tutorial",
                modifier = Modifier.size(28.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(56.dp))

            Text(
                text = "Welcome back, ${currentUser?.displayName ?: "User"}! \uD83D\uDC4B",
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

                            val isSubtopicCompleted =
                                (userProgressMap[subtopic.id]?.completed == true)
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

        NewQuestionsDialog(
            newQuestionsAvailable = viewModel.newQuestionsAvailable,
            newQuestionsSubtopicNames = viewModel.newQuestionsSubtopicNames,
            onDismiss = { currentUser?.uid?.let { viewModel.clearNewQuestionsFlag(it) } }
        )
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

@Composable
fun NewQuestionsDialog(
    newQuestionsAvailable: Boolean,
    newQuestionsSubtopicNames: List<String>,
    onDismiss: () -> Unit
) {
    if (newQuestionsAvailable) {
        val subtopicsText = formatSubtopicNames(newQuestionsSubtopicNames)

        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "🎉 New Questions Alert!",
                        style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }
            },
            text = {
                Text(
                    text = "Woohoo!! We've added new questions to $subtopicsText just for you!",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}

fun formatSubtopicNames(names: List<String>): String {
    return when {
        names.isEmpty() -> "various topics"
        names.size == 1 -> names[0]
        else -> {
            val allButLast = names.dropLast(1).joinToString(", ")
            val last = names.last()
            "$allButLast & $last"
        }
    }
}
