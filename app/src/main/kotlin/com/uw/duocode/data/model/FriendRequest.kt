package com.uw.duocode.data.model

enum class FriendRequestStatus {
    PENDING,
    ACCEPTED,
    REJECTED
}

data class FriendRequest(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderEmail: String = "",
    val senderProfilePictureUrl: String? = null,
    val receiverId: String = "",
    val receiverName: String = "",
    val receiverEmail: String = "",
    val status: String = FriendRequestStatus.PENDING.name,
    val createdAt: Long = System.currentTimeMillis()
) 