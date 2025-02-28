package com.uw.duocode.ui.screens.questions

import androidx.compose.runtime.getValue
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

    var currentQuestionIndex by mutableStateOf(0)
        private set

    fun loadQuestions(subtopicId: String) {
        isLoading = true
        error = null

        val db = FirebaseFirestore.getInstance()
        try {
            db.collection("questions")
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
        } catch (e: Exception) {
            error = "Error: ${e.message}"
            isLoading = false
        }
    }

    fun moveToNextQuestion() {
        currentQuestionIndex++
    }
}
