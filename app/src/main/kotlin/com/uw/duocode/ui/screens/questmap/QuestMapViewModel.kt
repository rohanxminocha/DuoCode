package com.uw.duocode.ui.screens.questmap

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.uw.duocode.data.model.SubtopicInfo
import com.uw.duocode.data.model.TopicInfo
import kotlinx.coroutines.launch


class QuestMapViewModel : ViewModel() {

    var topics by mutableStateOf<List<TopicInfo>>(emptyList())
        private set

    var subtopics by mutableStateOf<List<SubtopicInfo>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    init {
        fetchData()
    }

    fun fetchData() {
        isLoading = true
        error = null

        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            try {
                db.collection("topics")
                    .get()
                    .addOnSuccessListener { topicsResult ->
                        val fetchedTopics = topicsResult.map { it.toObject<TopicInfo>() }

                        db.collection("subtopics")
                            .get()
                            .addOnSuccessListener { subtopicsResult ->
                                val fetchedSubtopics =
                                    subtopicsResult.map { it.toObject<SubtopicInfo>() }

                                topics = fetchedTopics
                                subtopics = fetchedSubtopics
                                isLoading = false
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
}
