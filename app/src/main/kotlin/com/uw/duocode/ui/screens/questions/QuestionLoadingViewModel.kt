package com.uw.duocode.ui.screens.questions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.uw.duocode.data.model.DragAndDropQuestion
import com.uw.duocode.data.model.MatchQuestion
import com.uw.duocode.data.model.MultipleChoiceQuestion
import com.uw.duocode.data.model.Question


class QuestionLoadingViewModel : ViewModel() {

    var isLoading by mutableStateOf(true)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var questions by mutableStateOf<List<Question>>(emptyList())
        private set

    var currentQuestionIndex by mutableIntStateOf(0)
        private set

    private var correctAnswerCount by mutableIntStateOf(0)

    fun loadQuestions(subtopicId: String) {
        isLoading = true
        error = null
        currentQuestionIndex = 0
        correctAnswerCount = 0

        FirebaseFirestore.getInstance()
            .collection("questions")
            .whereEqualTo("subtopicId", subtopicId)
            .get()
            .addOnSuccessListener { result ->
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
    }

    fun onQuestionCompleted(isCorrect: Boolean) {
        if (isCorrect) correctAnswerCount++
        moveToNextQuestion()

        if (currentQuestionIndex >= questions.size) {
            updateSubtopicProgress()
        }
    }

    private fun moveToNextQuestion() {
        if (currentQuestionIndex < questions.size) {
            currentQuestionIndex++
        }
    }

    private fun updateSubtopicProgress() {
        val firstQuestion = questions.firstOrNull() ?: return
        val subtopicId = firstQuestion.subtopicId ?: return
        val threshold = 8
        val completed = (correctAnswerCount >= threshold)

        println("updateSubtopicProgress() called with subtopicId=$subtopicId, isCompleted=$completed")

        val db = FirebaseFirestore.getInstance()
        val subtopicRef = db.collection("subtopics").document(subtopicId)

        subtopicRef.update(
            mapOf(
                "correctAnswers" to correctAnswerCount,
                "completed" to completed
            )
        )
            .addOnSuccessListener {
                println("Subtopic progress updated: correctAnswers=$correctAnswerCount, isCompleted=$completed")
                subtopicRef.get().addOnSuccessListener { doc ->
                    val updatedCorrect = doc.getLong("correctAnswers") ?: 0
                    val updatedIsCompleted = doc.getBoolean("isCompleted") ?: false
                    println("Confirmed Firestore update: correctAnswers=$updatedCorrect, isCompleted=$updatedIsCompleted")
                }
            }
            .addOnFailureListener { e ->
                println("Failed to update subtopic progress: ${e.message}")
            }
    }
}
