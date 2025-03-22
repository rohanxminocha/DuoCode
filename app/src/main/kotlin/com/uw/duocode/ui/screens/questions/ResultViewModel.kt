package com.uw.duocode.ui.screens.questions

import androidx.lifecycle.ViewModel


class ResultViewModel(
    correctAnswerCount: Int,
    totalQuestions: Int,
    private val timeSpentSeconds: Long
) : ViewModel() {
    val percentage: Float = correctAnswerCount.toFloat() / totalQuestions.toFloat()

    val title: String
        get() = if (percentage >= 0.8f) "Good Job!" else "Try Again"

    val message: String
        get() = if (percentage >= 0.8f)
            "You got ${(percentage * 100).toInt()}% of the questions correct!"
        else
            "You got ${(percentage * 100).toInt()}% of the questions correct. Keep practicing!"

    val timeMessage: String
        get() {
            val minutes = timeSpentSeconds / 60
            val seconds = timeSpentSeconds % 60
            return "Time Spent: ${minutes}:${seconds.toString().padStart(2, '0')}"
        }
}
