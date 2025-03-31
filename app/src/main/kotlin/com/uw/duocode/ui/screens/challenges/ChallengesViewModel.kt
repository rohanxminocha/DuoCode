package com.uw.duocode.ui.screens.challenges

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.uw.duocode.data.model.UserSubtopicProgress
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

data class UserSubtopicProgressWithId(
    val id: String,
    val progress: UserSubtopicProgress
)

data class ChallengeData(
    val title: String = "",
    val subTitle: String = "",
    val completed: Boolean = false,
    val dateCompleted: Date? = null,
    val subtopicId: String = ""
)


class ChallengesViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {
    var challenges by mutableStateOf<List<ChallengeData>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    private val userId: String get() = auth.currentUser?.uid.toString()

    fun createChallengesFromProgress(
        progressItems: List<UserSubtopicProgressWithId>,
        challengeMap: Map<String, DocumentSnapshot>
    ): List<Pair<String, ChallengeData>> {
        val thresholds = listOf(
            "Beginner" to 2,
            "Intermediate" to 5,
            "Expert" to 10
        )
        val result = mutableListOf<Pair<String, ChallengeData>>()

        progressItems.forEach { progressWithId ->
            thresholds.forEach { (levelName, threshold) ->
                if (progressWithId.progress.correctAnswers >= threshold) {
                    val challengeId = "${progressWithId.id}-$levelName"
                    val snapshot = challengeMap[challengeId]
                    val challengeData = snapshot?.toObject(ChallengeData::class.java)
                    if (challengeData != null && !challengeData.completed) {
                        result.add(challengeId to challengeData)
                    }
                }
            }
        }
        return result
    }

    fun loadChallenges() {
        isLoading = true
        error = null

        val userDocRef = db.collection("users").document(userId)
        userDocRef.collection("subtopics").get()
            .addOnSuccessListener { progressSnapshot ->
                val progressItems = progressSnapshot.documents.mapNotNull { doc ->
                    doc.toObject(UserSubtopicProgress::class.java)?.let { progress ->
                        UserSubtopicProgressWithId(doc.id, progress)
                    }
                }
                userDocRef.collection("challenges").get()
                    .addOnSuccessListener { challengeSnapshot ->
                        val challengeMap = challengeSnapshot.documents.associateBy { it.id }
                        val challengesToUpdate = createChallengesFromProgress(progressItems, challengeMap)
                        val batch = db.batch()
                        challengesToUpdate.forEach { (challengeId, _) ->
                            val challengeDocRef = userDocRef.collection("challenges").document(challengeId)
                            batch.update(challengeDocRef, mapOf(
                                "completed" to true,
                                "dateCompleted" to Date()
                            ))
                        }
                        batch.commit()
                            .addOnSuccessListener {
                                userDocRef.collection("challenges").get()
                                    .addOnSuccessListener { updatedSnapshot ->
                                        challenges = updatedSnapshot.documents.mapNotNull {
                                            it.toObject(ChallengeData::class.java)
                                        }
                                        isLoading = false
                                    }
                                    .addOnFailureListener { e ->
                                        error = "Error loading challenges: ${e.message}"
                                        isLoading = false
                                    }
                            }
                            .addOnFailureListener { e ->
                                error = "Error updating challenges: ${e.message}"
                                isLoading = false
                            }
                    }
                    .addOnFailureListener { e ->
                        error = "Error loading existing challenges: ${e.message}"
                        isLoading = false
                    }
            }
            .addOnFailureListener { e ->
                error = "Error loading subtopic progress: ${e.message}"
                isLoading = false
            }
    }

    fun countCompletedDaysThisMonth(): Int {
        val now = LocalDate.now()
        val currentYear = now.year
        val currentMonth = now.monthValue

        return challenges.filter { it.completed && it.dateCompleted != null }
            .map { it.dateCompleted!! }
            .map { date ->
                date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            }
            .filter { localDate -> localDate.year == currentYear && localDate.monthValue == currentMonth }
            .map { localDate -> localDate.dayOfMonth }
            .toSet()
            .size
    }
}