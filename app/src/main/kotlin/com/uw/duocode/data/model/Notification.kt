package com.uw.duocode.data.model

data class Notification(
    val notificationId: String = "",
    val userId: String = "",
    val time: Long = System.currentTimeMillis(),
    val message: String = "Time to practice DSA!",
    val isEnabled: Boolean = true
)
