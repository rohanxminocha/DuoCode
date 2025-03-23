package com.uw.duocode.ui.screens.challenges

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uw.duocode.data.model.UserSubtopicProgress

data class ChallengeData(
    val title: String,
    val subTitle: String,
    val isCompleted: Boolean
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

    private val subtopicsMapping = mapOf(
        "1dL9iqYFfmv0KjubQN1k" to "1-Dimensional DP",
        "3KFDkaMUldzOYB265HhU" to "Static Arrays",
        "3Mb9iVeOqU475Zj3BwyA" to "Queues",
        "4ZYUDUddEfItaBKCFivf" to "Dynamic Arrays",
        "7QBjY3TB0CVu3ZrR1Oh2" to "Stacks",
        "ANXFboJHfR4Mfc2lv42G" to "Search Array",
        "CQ8NqMZAKQCc9XrEo4q" to "Breadth-First Search",
        "GABlt2fzYt99zyB9WrDL" to "Search Range",
        "Mq9KI04lKaWEyB9OT6cP" to "Binary Search Tree",
        "OaAD5Flszoi2FMWhaIfY" to "Doubly Linked Lists",
        "Oj1HrmRv6qyslpc8t52B" to "Heap Properties",
        "PCNnC1lDdtyQEzTQVglL" to "Adjacency List",
        "WLb6xyl82xGisd7msKYY" to "Binary Tree",
        "WPsWVNS2rVKG2zcfeH1D" to "Heapify",
        "dCE0IT7YZ3RZMPLCfd8X" to "Hashing",
        "qWYdCytlaOhoeCaGYX73" to "Intro to Graphs",
        "sfFzjXaPQfL2fiFLG80h" to "Singly Linked Lists",
        "snaPr7iMCcZy1IpoewHM" to "Depth-First Search",
        "wvAjMA3fQrK9ReRQDnPq" to "2-Dimensional DP"
    )

    fun loadChallenges() {
        isLoading = true
        error = null

        db.collection("users")
            .document(userId)
            .collection("subtopics")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val progressList = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(UserSubtopicProgress::class.java)?.apply {
                        id = doc.id
                    }
                }

                challenges = createChallengesFromProgress(progressList)
                isLoading = false
            }
            .addOnFailureListener { e ->
                error = "Error loading challenges: ${e.message}"
                isLoading = false
            }
    }
    
    // This method is extracted for easier testing
    internal fun createChallengesFromProgress(progressList: List<UserSubtopicProgress>): List<ChallengeData> {
        val challengeList = mutableListOf<ChallengeData>()
        progressList.forEach { progress ->
            val subtopicName = subtopicsMapping[progress.id] ?: progress.id
            listOf(
                5 to "Beginner",
                10 to "Intermediate",
                15 to "Expert"
            ).forEach { (threshold, levelName) ->
                val isCompleted = progress.correctAnswers >= threshold
                challengeList.add(
                    ChallengeData(
                        title = "$subtopicName $levelName",
                        subTitle = "Finish $threshold questions in $subtopicName",
                        isCompleted = isCompleted
                    )
                )
            }
        }
        return challengeList
    }
}