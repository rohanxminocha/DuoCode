package com.uw.duocode.ui.screens.questions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.uw.duocode.data.model.DragAndDropQuestion
import com.uw.duocode.data.model.MatchQuestion
import com.uw.duocode.data.model.MultipleChoiceQuestion
import com.uw.duocode.data.model.Question
import java.util.Calendar


class QuestionLoadingViewModel : ViewModel() {

    var isLoading by mutableStateOf(true)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var questions by mutableStateOf<List<Question>>(emptyList())
        private set

    var currentQuestionIndex by mutableIntStateOf(0)
        private set

    var correctAnswerCount by mutableIntStateOf(0)

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
            updateCompletedQuestionsCount()
        }
    }

    private fun updateCompletedQuestionsCount() {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val db = FirebaseFirestore.getInstance()
        val userQuery = db.collection("users").whereEqualTo("uid", currentUser.uid)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val todayMidnight = calendar.timeInMillis

        userQuery.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val userDoc = querySnapshot.documents.first()
                val userDocRef = userDoc.reference
                val lastQuestCompletedDate = userDoc.getLong("lastQuestCompletedDate") ?: 0L

                if (lastQuestCompletedDate < todayMidnight) {
                    userDocRef.update(
                        "questionsCompletedToday", 1,
                        "lastQuestCompletedDate", System.currentTimeMillis(),
                        "totalQuestionsAttempted", FieldValue.increment(1)
                    )
                } else {
                    userDocRef.update(
                        "questionsCompletedToday", FieldValue.increment(1),
                        "lastQuestCompletedDate", System.currentTimeMillis(),
                        "totalQuestionsAttempted", FieldValue.increment(1)
                    )
                }
            }
        }
    }

    private fun updateUserSubtopicProgress() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            println("No user logged in, cannot update user subtopic progress.")
            return
        }
        val firstQuestion = questions.firstOrNull() ?: return
        val subtopicId = firstQuestion.subtopicId ?: return
        val threshold = 8
        val completed = (correctAnswerCount >= threshold)

        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .whereEqualTo("uid", currentUser.uid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    println("User document not found")
                    return@addOnSuccessListener
                }
                val userDocId = querySnapshot.documents.first().id
                val docRef = db.collection("users")
                    .document(userDocId)
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
            .addOnFailureListener { e ->
                println("Failed to query user document: ${e.message}")
            }
    }
}
