package com.uw.duocode.data.model

data class Friend(
    val id: String = "",
    val userId: String = "",
    val friendId: String = "",
    val friendName: String = "",
    val friendEmail: String = "",
    val friendProfilePictureUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) 