package com.uw.duocode.data.model

import com.google.firebase.firestore.DocumentId

data class TopicInfo(
    @DocumentId
    var id: String = "",
    var name: String = "",
    var order: Int = 0,
    var iconKey: String = "code",
    var subtopics: MutableList<String> = mutableListOf()
)