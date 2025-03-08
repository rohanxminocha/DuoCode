package com.uw.duocode.data.model

import com.google.firebase.firestore.DocumentId

data class UserSubtopicProgress(
    @DocumentId
    var id: String = "", // same as the subtopicId
    var completed: Boolean = false,
    var correctAnswers: Int = 0
)