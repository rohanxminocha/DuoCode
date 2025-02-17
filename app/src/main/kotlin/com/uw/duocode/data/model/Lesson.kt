package com.uw.duocode.data.model

import com.google.firebase.firestore.DocumentId

data class LessonInfo(
    @DocumentId
    val id: String = "",
    val description: String = "",
    val topicId: String = ""
)