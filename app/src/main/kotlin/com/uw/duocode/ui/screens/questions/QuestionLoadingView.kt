package com.uw.duocode.ui.screens.questions

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.uw.duocode.data.model.DragAndDropQuestion
import com.uw.duocode.data.model.MatchQuestion
import com.uw.duocode.data.model.MultipleChoiceQuestion


@Composable
fun QuestionLoadingView(
    navController: NavHostController,
    subtopicId: String,
    viewModel: QuestionLoadingViewModel = viewModel()
) {
    LaunchedEffect(key1 = subtopicId) {
        if (viewModel.questions.isEmpty()) {
            viewModel.loadQuestions(subtopicId)
        }
    }

    when {
        viewModel.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        viewModel.error != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = viewModel.error!!)
            }
        }

        viewModel.questions.isEmpty() -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No questions found")
            }
        }

        viewModel.currentQuestionIndex >= viewModel.questions.size -> {
            // Navigate to the results screen when done
            ResultView(
                navController = navController,
                viewModel = remember {
                    ResultViewModel(
                        correctAnswerCount = viewModel.correctAnswerCount,
                        totalQuestions = viewModel.questions.size
                    )
                }
            )
        }

        else -> {
            val currentQuestion = viewModel.questions[viewModel.currentQuestionIndex]
            val total = viewModel.questions.size
            val questionKey = viewModel.currentQuestionIndex
            val progress = viewModel.currentQuestionIndex.toFloat() / total.toFloat()

            when (currentQuestion) {
                is MultipleChoiceQuestion -> {
                    MultipleChoiceView(
                        navController = navController,
                        viewModel = remember(questionKey) {
                            MultipleChoiceViewModel(
                                questionText = currentQuestion.description ?: "",
                                options = currentQuestion.options ?: emptyList(),
                                correctAnswer = currentQuestion.correctAnswer ?: emptyList(),
                                onQuestionCompleted = { isCorrect ->
                                    viewModel.onQuestionCompleted(isCorrect)
                                },
                                progress = progress
                            )
                        }
                    )
                }

                is MatchQuestion -> {
                    MatchView(
                        navController = navController,
                        matchViewModel = remember(questionKey) {
                            MatchViewModel(
                                questionText = currentQuestion.description ?: "",
                                correctPairs = currentQuestion.matches ?: emptyMap(),
                                onQuestionCompleted = { isCorrect ->
                                    viewModel.onQuestionCompleted(isCorrect)
                                },
                                progress = progress
                            )
                        }
                    )
                }

                is DragAndDropQuestion -> {
                    DragDropView(
                        navController = navController,
                        viewModel = remember(questionKey) {
                            DragDropViewModel(
                                questionText = currentQuestion.description ?: "",
                                initialSteps = currentQuestion.options ?: emptyList(),
                                onQuestionCompleted = { isCorrect ->
                                    viewModel.onQuestionCompleted(isCorrect)
                                },
                                progress = progress
                            )
                        }
                    )
                }
            }
        }
    }
}
