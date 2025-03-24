package com.uw.duocode.data.model

data class Notification(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "" // e.g., "dailyReminder" or "streakAlert"
)
