package com.uw.duocode.data.model

import com.google.firebase.firestore.DocumentId

data class SubtopicInfo (
    @DocumentId
    var id: String = "",
    var name: String = "",
    var topicId: String = ""
)