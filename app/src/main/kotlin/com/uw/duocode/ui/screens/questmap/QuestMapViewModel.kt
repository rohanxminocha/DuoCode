package com.uw.duocode.ui.screens.questmap

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.uw.duocode.data.model.TopicInfo
import com.uw.duocode.data.model.SubtopicInfo
import kotlinx.coroutines.launch

class QuestMapViewModel : ViewModel() {

    var topics by mutableStateOf<List<TopicInfo>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()

            try {
                db.collection("topics")
                    .get()
                    .addOnSuccessListener { topicsRes ->
                        topics = topicsRes.map { it.toObject<TopicInfo>() }

                        db.collection("subtopics")
                            .whereIn("topicId", topics.map { it.id })
                            .get()
                            .addOnSuccessListener { result ->
                                val subtopics = result.map { it.toObject<SubtopicInfo>() }
                                subtopics.forEach { subTopic ->
                                    val topicIdx = topics.indexOfFirst { it.id == subTopic.topicId }
                                    if (topicIdx >= 0) {
                                        topics[topicIdx].subtopics.add(subTopic)
                                    }
                                }
                                isLoading = false
                            }
                            .addOnFailureListener { e ->
                                error = "Error loading lessons: ${e.message}"
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
