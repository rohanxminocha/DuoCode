package com.uw.duocode.ui.screens.questions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.uw.duocode.data.model.DragAndDropQuestion
import com.uw.duocode.data.model.MatchQuestion
import com.uw.duocode.data.model.MultipleChoiceQuestion
import com.uw.duocode.data.model.Question
import java.util.Calendar
import java.util.Date

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
        updateCompletedQuestionsCount()
    }

    private fun updateCompletedQuestionsCount() {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(currentUser.uid)
        
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val todayMidnight = calendar.timeInMillis
        
        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val lastQuestCompletedDate = document.getLong("lastQuestCompletedDate") ?: 0L
                
                if (lastQuestCompletedDate < todayMidnight) {
                    userRef.update(
                        "questionsCompletedToday", 1,
                        "lastQuestCompletedDate", System.currentTimeMillis(),
                        "totalQuestionsAttempted", FieldValue.increment(1)
                    )
                } else {
                    userRef.update(
                        "questionsCompletedToday", FieldValue.increment(1),
                        "lastQuestCompletedDate", System.currentTimeMillis(),
                        "totalQuestionsAttempted", FieldValue.increment(1)
                    )
                }
            }
        }
    }
}
