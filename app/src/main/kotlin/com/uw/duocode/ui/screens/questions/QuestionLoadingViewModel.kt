package com.uw.duocode.ui.screens.questions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
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

    private var totalQuizTimeSeconds by mutableLongStateOf(0L)

    private var questionStartTime: Long = System.currentTimeMillis()

    private var lastQuestionCorrect by mutableStateOf(false)

    fun loadQuestions(subtopicId: String) {
        isLoading = true
        error = null
        currentQuestionIndex = 0
        correctAnswerCount = 0
        totalQuizTimeSeconds = 0L
        questionStartTime = System.currentTimeMillis()

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
        lastQuestionCorrect = isCorrect
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

        val timeSpentMillis = System.currentTimeMillis() - questionStartTime
        val timeSpentForQuestionMinutes = timeSpentMillis.toDouble() / (1000 * 60)
        totalQuizTimeSeconds += timeSpentMillis / 1000

        userQuery.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val userDoc = querySnapshot.documents.first()
                val userDocRef = userDoc.reference
                val lastQuestCompletedDate = userDoc.getLong("lastQuestCompletedDate") ?: 0L

                val correctIncrement = if (lastQuestionCorrect) 1 else 0

                if (lastQuestCompletedDate < todayMidnight) {
                    userDocRef.update(
                        "questionsCompletedToday", 1,
                        "lastQuestCompletedDate", System.currentTimeMillis(),
                        "totalQuestionsAttempted", FieldValue.increment(1),
                        "totalCorrectAnswers", correctIncrement,
                        "totalTimeSpentInMinutes", FieldValue.increment(timeSpentForQuestionMinutes)
                    )
                } else {
                    userDocRef.update(
                        "questionsCompletedToday", FieldValue.increment(1),
                        "lastQuestCompletedDate", System.currentTimeMillis(),
                        "totalQuestionsAttempted", FieldValue.increment(1),
                        "totalCorrectAnswers", FieldValue.increment(correctIncrement.toLong()),
                        "totalTimeSpentInMinutes", FieldValue.increment(timeSpentForQuestionMinutes)
                    )
                }

                questionStartTime = System.currentTimeMillis()  // reset timer for next question
            }
        }
    }

    private fun updateUserSubtopicProgress() {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val firstQuestion = questions.firstOrNull() ?: return
        val subtopicId = firstQuestion.subtopicId ?: return
        val threshold = 8
        val passedNow = correctAnswerCount >= threshold
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(currentUser.uid)
            .collection("subtopics")
            .document(subtopicId)
            .get()
            .addOnSuccessListener { subtopicDoc ->
                val alreadyCompleted = subtopicDoc.getBoolean("completed") ?: false
                if (!alreadyCompleted) {
                    val data = mapOf(
                        "completed" to passedNow,
                        "correctAnswers" to correctAnswerCount
                    )
                    subtopicDoc.reference.set(data, SetOptions.merge())
                        .addOnSuccessListener {
                            updateDailyStreak()
                        }
                        .addOnFailureListener { e ->
                            println("Failed to update subtopic progress for $subtopicId: ${e.message}")
                        }
                } else {
                    updateDailyStreak()
                }
            }
            .addOnFailureListener { e ->
                println("Failed to get subtopic progress for $subtopicId: ${e.message}")
            }
    }

    private fun updateDailyStreak() {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(currentUser.uid)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val todayMidnight = calendar.timeInMillis
        val yesterdayMidnight = todayMidnight - 24 * 60 * 60 * 1000L

        userRef.get().addOnSuccessListener { userDoc ->
            val lastStreakDate = userDoc.getLong("lastStreakDate") ?: 0L
            val currentStreak = (userDoc.getLong("currentStreak") ?: 0L).toInt()
            val longestStreak = (userDoc.getLong("longestStreak") ?: 0L).toInt()

            if (lastStreakDate >= todayMidnight) {
                return@addOnSuccessListener
            }

            val newCurrentStreak = if (lastStreakDate >= yesterdayMidnight) currentStreak + 1 else 1
            val newLongestStreak =
                if (newCurrentStreak > longestStreak) newCurrentStreak else longestStreak

            userRef.update(
                "currentStreak", newCurrentStreak,
                "longestStreak", newLongestStreak,
                "lastStreakDate", todayMidnight
            )
        }
    }
}
