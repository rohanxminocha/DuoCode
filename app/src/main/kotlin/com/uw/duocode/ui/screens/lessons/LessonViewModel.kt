package com.uw.duocode.ui.screens.lessons

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.uw.duocode.data.model.LessonInfo
import com.uw.duocode.data.model.TopicInfo


class LessonViewModel : ViewModel() {
    var description by mutableStateOf("Loading...")
        private set

    var topicName by mutableStateOf("Loading...")
        private set

    var iconKey by mutableStateOf("default")
        private set

    var imageUrl by mutableStateOf("") // New property for lesson image URL
        private set

    var isLoading by mutableStateOf(true)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun loadLessonData(topicId: String) {
        isLoading = true
        error = null

        val db = FirebaseFirestore.getInstance()
        try {
            db.collection("lessons")
                .whereEqualTo("topicId", topicId)
                .limit(1)
                .get()
                .addOnSuccessListener { lessonDoc ->
                    if (lessonDoc.isEmpty) {
                        error = "Lesson not found"
                        isLoading = false
                        return@addOnSuccessListener
                    }
                    val lesson = lessonDoc.documents[0].toObject<LessonInfo>()
                    description = lesson?.description ?: "No description available"
                    imageUrl = lesson?.imageUrl ?: ""

                    db.collection("topics")
                        .document(topicId)
                        .get()
                        .addOnSuccessListener { topicDoc ->
                            val topic = topicDoc.toObject<TopicInfo>()
                            topicName = topic?.name ?: "Topic Name"
                            iconKey = topic?.iconKey ?: "default"
                            isLoading = false
                        }
                        .addOnFailureListener { e ->
                            error = "Error loading topic: ${e.message}"
                            isLoading = false
                        }
                }
                .addOnFailureListener { e ->
                    error = "Error loading lesson: ${e.message}"
                    isLoading = false
                }
        } catch (e: Exception) {
            error = "Error: ${e.message}"
            isLoading = false
        }
    }
}
