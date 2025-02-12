package com.uw.duocode.data.model

data class Question(
    val questionId: String = "",
    val topic: String = "",
    val difficulty: Difficulty = Difficulty.EASY,
    val type: QuestionType = QuestionType.MULTIPLE_CHOICE,
    val description: String = "",
    val options: List<String> = emptyList(), // for MCQs
    val correctAnswer: String = "",
    val hints: List<String> = emptyList(), // ranked hints
    val isAnsweredCorrectly: Boolean = false
)

enum class Difficulty { EASY, MEDIUM, HARD }
enum class QuestionType { MULTIPLE_CHOICE, MATCHING, FILL_IN_THE_BLANK }
