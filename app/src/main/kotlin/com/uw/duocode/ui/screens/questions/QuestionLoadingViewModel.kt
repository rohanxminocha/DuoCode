package com.uw.duocode.ui.screens.questions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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
            updateUserSubtopicProgress()
        }
    }

    private fun moveToNextQuestion() {
        if (currentQuestionIndex < questions.size) {
            currentQuestionIndex++
        }
    }

    private fun updateUserSubtopicProgress() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            println("No user logged in, cannot update user subtopic progress.")
            return
        }

        val firstQuestion = questions.firstOrNull() ?: return
        val subtopicId = firstQuestion.subtopicId ?: return
        val threshold = 8
        val completed = (correctAnswerCount >= threshold)
        println("updateUserSubtopicProgress() -> user=${user.uid}, subtopicId=$subtopicId, completed=$completed")

        val docRef = FirebaseFirestore.getInstance()
            .collection("users")
            .document(user.uid)
            .collection("subtopics")
            .document(subtopicId)

        val data = mapOf(
            "completed" to completed,
            "correctAnswers" to correctAnswerCount
        )

        docRef.set(data, SetOptions.merge())
            .addOnSuccessListener {
                println("User subtopic progress updated: correctAnswers=$correctAnswerCount, completed=$completed")
            }
            .addOnFailureListener { e ->
                println("Failed to update user subtopic progress: ${e.message}")
            }
    }
}
