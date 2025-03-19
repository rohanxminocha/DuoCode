package com.uw.duocode.ui.screens.questions

import androidx.lifecycle.ViewModel

class ResultViewModel(
    private val correctAnswerCount: Int,
    private val totalQuestions: Int
) : ViewModel() {
    val percentage: Float = correctAnswerCount.toFloat() / totalQuestions.toFloat()

    val title: String
        get() = if (percentage >= 0.5f) "Good Job!" else "Try Again"

    val message: String
        get() = if (percentage >= 0.5f)
            "You got ${(percentage * 100).toInt()}% of the questions correct!"
        else
            "You got ${(percentage * 100).toInt()}% of the questions correct. Keep practicing!"
}