package com.uw.duocode.data.model

data class Achievement(
    val achievementId: String = "",
    val title: String = "",
    val description: String = "",
    val isUnlocked: Boolean = false,
    val totalCorrectAnswersRequired: Int? = null,
    val noHintsPercentageRequired: Double? = null
)
