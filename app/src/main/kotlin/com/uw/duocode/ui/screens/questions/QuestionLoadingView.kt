package com.uw.duocode.ui.screens.questions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.uw.duocode.DragDropView
import com.uw.duocode.data.model.DragAndDropQuestion
import com.uw.duocode.data.model.MatchQuestion
import com.uw.duocode.data.model.MultipleChoiceQuestion

@Composable
fun QuestionLoadingView(
    navController: NavHostController,
    subtopicId: String,
    viewModel: QuestionLoadingViewModel = viewModel()
) {
    LaunchedEffect(subtopicId) {
        viewModel.loadQuestions(subtopicId)
    }

    when {
        viewModel.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        viewModel.error != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(viewModel.error!!)
            }
        }

        viewModel.questions.isEmpty() -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No questions found")
            }
        }

        viewModel.currentQuestionIndex >= viewModel.questions.size -> {
            // Navigate to the results screen when done
            ResultView(navController = navController)
        }

        else -> {
            val currentQuestion = viewModel.questions[viewModel.currentQuestionIndex]
            val numQuestions = viewModel.questions.size
            val progress = viewModel.currentQuestionIndex.toFloat() / numQuestions.toFloat()

            when (currentQuestion) {
                is MultipleChoiceQuestion -> {
                    MultipleChoiceView(
                        navController = navController,
                        viewModel = remember {
                            MultipleChoiceViewModel(
                                questionText = currentQuestion.description ?: "",
                                options = currentQuestion.options ?: emptyList(),
                                correctAnswer = currentQuestion.correctAnswer ?: listOf(),
                                onQuestionCompleted = { viewModel.moveToNextQuestion() },
                                progress = progress
                            )
                        }
                    )
                }

                is MatchQuestion -> {
                    MatchView(
                        navController = navController,
                        matchViewModel = remember {
                            MatchViewModel(
                                questionText = currentQuestion.description ?: "",
                                correctPairs = currentQuestion.matches ?: emptyMap(),
                                onQuestionCompleted = { viewModel.moveToNextQuestion() },
                                progress = progress
                            )
                        }
                    )
                }

                is DragAndDropQuestion -> {
                    DragDropView(
                        navController = navController,
                        viewModel = remember {
                            DragDropViewModel(
                                questionText = currentQuestion.description ?: "",
                                initialSteps = currentQuestion.options ?: emptyList(),
                                onQuestionCompleted = { viewModel.moveToNextQuestion() },
                                progress = progress
                            )
                        }
                    )
                }
            }
        }
    }
}
