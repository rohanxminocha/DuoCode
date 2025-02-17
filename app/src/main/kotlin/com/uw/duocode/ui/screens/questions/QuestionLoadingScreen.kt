package com.uw.duocode.ui.screens.questions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import com.uw.duocode.DragDropView
import com.uw.duocode.data.model.DragAndDropQuestion
import com.uw.duocode.data.model.MatchQuestion
import com.uw.duocode.data.model.MultipleChoiceQuestion
import com.uw.duocode.data.model.Question

@Composable
fun QuestionLoadingScreen(
    navController: NavHostController,
    subtopicId: String
) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var questions by remember { mutableStateOf<List<Question>>(emptyList()) }

    LaunchedEffect(subtopicId) {
        val db = FirebaseFirestore.getInstance()
        try {
            db.collection("questions")
                .whereEqualTo("subtopicId", subtopicId)
                .get()
                .addOnSuccessListener { result ->
                    // Convert documents to Question objects and shuffle
                    questions = result.documents.mapNotNull { doc ->
                        when (doc.getString("questionType")) {
                            "MULTIPLE_CHOICE" -> doc.toObject(MultipleChoiceQuestion::class.java)
                            "MATCHING" -> doc.toObject(MatchQuestion::class.java)
                            "DRAG_DROP" -> doc.toObject(DragAndDropQuestion::class.java)
                            else -> null
                        }
                    }.shuffled()
                    isLoading = false
                }
                .addOnFailureListener { e ->
                    error = "Error loading questions: ${e.message}"
                    isLoading = false
                }
        } catch (e: Exception) {
            error = "Error: ${e.message}"
            isLoading = false
        }
    }

    when {
        isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        error != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(error!!)
            }
        }
        questions.isEmpty() -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No questions found")
            }
        }
        currentQuestionIndex >= questions.size -> {
            ResultView(navController = navController)
        }
        else -> {
            val currentQuestion = questions[currentQuestionIndex]
            val numQuestions = questions.size
            val progress = currentQuestionIndex.toFloat() / numQuestions.toFloat()
            
            when (currentQuestion) {
                is MultipleChoiceQuestion -> {
                    MultipleChoiceView(
                        navController = navController,
                        viewModel = remember {
                            MultipleChoiceViewModel(
                                questionText = currentQuestion.description ?: "",
                                options = currentQuestion.options ?: emptyList(),
                                correctAnswer = currentQuestion.correctAnswer ?: listOf(),
                                onQuestionCompleted = { currentQuestionIndex++ },
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
                                onQuestionCompleted = { currentQuestionIndex++ },
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
                                onQuestionCompleted = { currentQuestionIndex++ },
                                progress = progress
                            )
                        }
                    )
                }
            }
        }
    }
}