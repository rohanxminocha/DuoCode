package com.uw.duocode.ui.screens.questions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.uw.duocode.ui.components.CheckContinueButton
import com.uw.duocode.ui.components.QuestionTopBar
import com.uw.duocode.ui.components.ResultBanner
import sh.calvin.reorderable.ReorderableColumn


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DragDropView(
    navController: NavHostController,
    viewModel: DragDropViewModel = viewModel()
) {
    val haptic = LocalHapticFeedback.current

    val steps = viewModel.steps
    val questionText = viewModel.questionText
    val answerChecked = viewModel.answerChecked
    val isAnswerCorrect = viewModel.isAnswerCorrect
    val progress = viewModel.progress

    Scaffold(
        topBar = {
            QuestionTopBar(
                navController = navController,
                progress = progress
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                CheckContinueButton(
                    text = if (!answerChecked) "CHECK" else "CONTINUE",
                    onClick = {
                        if (!answerChecked) {
                            viewModel.checkAnswer()
                        } else {
                            viewModel.continueToNext()
                        }
                    },
                    enabled = true,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .zIndex(1f)
                )
                if (answerChecked) {
                    ResultBanner(
                        isCorrect = isAnswerCorrect,
                        message = if (isAnswerCorrect) "Correct!" else "Incorrect!",
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .zIndex(0f)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = questionText,
                fontSize = 25.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 25.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            ReorderableColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                list = steps,
                onSettle = { fromIndex, toIndex ->
                    viewModel.onSwapSteps(fromIndex, toIndex)
                },
                onMove = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                },
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) { _, step, _ ->
                key(step) {
                    OutlinedCard(
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (!answerChecked)
                                    Modifier.draggableHandle(
                                        onDragStarted = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        },
                                        onDragStopped = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        }
                                    )
                                else Modifier
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = step,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(vertical = 8.dp),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
