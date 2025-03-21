package com.uw.duocode.data.model

data class User(
    val userId: String = "",
    val uid: String = "",
    val email: String = "",
    val profilePictureUrl: String? = null,
    val level: String = "Apprentice Coder",
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastStreakDate: Long = 0L,
    val lastLogin: Long = System.currentTimeMillis(),
    val notificationEnabled: Boolean = true,
    val totalQuestionsAttempted: Int = 0,
    val totalCorrectAnswers: Int = 0,
    val totalTimeSpentInMinutes: Long = 0L,
    val friendIds: List<String> = emptyList(),
    val questionsCompletedToday: Int = 0,
    val lastQuestCompletedDate: Long = 0L
)
