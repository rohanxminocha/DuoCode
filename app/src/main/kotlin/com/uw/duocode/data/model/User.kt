package com.uw.duocode.data.model

data class User(
    val userId: String = "",
    val email: String = "",
    val username: String = "",
    val profilePictureUrl: String? = null,
    val level: String = "",
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val completedQuests: List<String> = emptyList(),
    val lastLogin: Long = System.currentTimeMillis(),
    val notificationEnabled: Boolean = true,
    val totalQuestionsAttempted: Int = 0,
    val totalCorrectAnswers: Int = 0,
    val totalTimeSpent: Long = 0L
)
