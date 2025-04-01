package com.uw.duocode.ui.screens.questmap

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.uw.duocode.data.model.SubtopicInfo
import com.uw.duocode.data.model.TopicInfo
import com.uw.duocode.data.model.UserSubtopicProgress
import kotlinx.coroutines.launch


class QuestMapViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    var topics by mutableStateOf<List<TopicInfo>>(emptyList())
        private set

    var subtopics by mutableStateOf<List<SubtopicInfo>>(emptyList())
        private set

    var userSubtopicsProgress by mutableStateOf<List<UserSubtopicProgress>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var newQuestionsAvailable by mutableStateOf(false)
        private set

    private var newQuestionsSubtopics by mutableStateOf<List<String>>(emptyList())

    var newQuestionsSubtopicNames by mutableStateOf<List<String>>(emptyList())
        private set

    init {
        fetchData()
    }

    fun fetchData() {
        isLoading = true
        error = null

        viewModelScope.launch {
            try {
                val topicsRef = db.collection("topics")
                val subtopicsRef = db.collection("subtopics")

                // 1) Fetch topics
                topicsRef.get()
                    .addOnSuccessListener { topicsResult ->
                        topics = topicsResult.map { it.toObject<TopicInfo>() }
                        // 2) Then fetch subtopics
                        subtopicsRef.get()
                            .addOnSuccessListener { subtopicsResult ->
                                subtopics = subtopicsResult.map { it.toObject<SubtopicInfo>() }
                                // 3) Now that subtopics are loaded, fetch user progress...
                                fetchUserSubtopicProgress()
                                // ...and also check for new questions
                                val currentUser = auth.currentUser
                                if (currentUser != null) {
                                    checkForNewQuestions(currentUser.uid)
                                } else {
                                    isLoading = false
                                }
                            }
                            .addOnFailureListener { e ->
                                error = "Error loading subtopics: ${e.message}"
                                isLoading = false
                            }
                    }
                    .addOnFailureListener { e ->
                        error = "Error loading topics: ${e.message}"
                        isLoading = false
                    }
            } catch (e: Exception) {
                error = "Error: ${e.message}"
                isLoading = false
            }
        }
    }

    private fun fetchUserSubtopicProgress() {
        val user = auth.currentUser
        if (user == null) {
            isLoading = false
            return
        }
        val userSubtopicsRef = db.collection("users")
            .document(user.uid)
            .collection("subtopics")

        userSubtopicsRef.get()
            .addOnSuccessListener { snap ->
                val progressList = snap.documents.mapNotNull { doc ->
                    doc.toObject<UserSubtopicProgress>()
                }
                userSubtopicsProgress = progressList
                isLoading = false
            }
            .addOnFailureListener { e ->
                error = "Error loading user progress: ${e.message}"
                isLoading = false
            }
    }

    fun checkForNewQuestions(userId: String) {
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    newQuestionsAvailable = document.getBoolean("newQuestionsAvailable") ?: false
                    val subtopicIds =
                        document.get("newQuestionsSubtopics") as? List<String> ?: emptyList()
                    newQuestionsSubtopics = subtopicIds

                    val mappedNames = subtopics.filter { it.id in subtopicIds }
                        .map { it.name }
                        .distinct()

                    newQuestionsSubtopicNames = mappedNames
                } else {
                    newQuestionsAvailable = false
                    newQuestionsSubtopics = emptyList()
                    newQuestionsSubtopicNames = emptyList()
                }
            }
            .addOnFailureListener {
                newQuestionsAvailable = false
                newQuestionsSubtopics = emptyList()
                newQuestionsSubtopicNames = emptyList()
            }
    }

    fun clearNewQuestionsFlag(userId: String) {
        db.collection("users").document(userId)
            .update(
                mapOf(
                    "newQuestionsAvailable" to false,
                    "newQuestionsSubtopics" to emptyList<String>()
                )
            )
            .addOnSuccessListener {
                newQuestionsAvailable = false
                newQuestionsSubtopics = emptyList()
                newQuestionsSubtopicNames = emptyList()
            }
    }
}
