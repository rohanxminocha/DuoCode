package com.uw.duocode.data.model

import com.google.firebase.firestore.DocumentId

data class TopicInfo(
    @DocumentId
    var id: String = "",
    var name: String = "",
    // val description: String,
    var subtopics: MutableList<SubtopicInfo> = mutableListOf()
)