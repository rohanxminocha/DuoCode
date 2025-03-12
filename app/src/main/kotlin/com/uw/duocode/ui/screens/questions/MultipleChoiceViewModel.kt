package com.uw.duocode.ui.screens.questions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel


class MultipleChoiceViewModel(
    val questionText: String,
    val options: List<String>,
    val correctAnswer: List<Int>,
    private val onQuestionCompleted: (Boolean) -> Unit,
    val progress: Float
) : ViewModel() {

    var selectedOption by mutableStateOf<String?>(null)
        private set

    var answerChecked by mutableStateOf(false)
        private set

    var isAnswerCorrect by mutableStateOf(false)
        private set

    fun onOptionSelected(option: String) {
        if (!answerChecked) {
            selectedOption = option
        }
    }

    fun checkAnswer() {
        if (!answerChecked && selectedOption != null) {
            answerChecked = true
            isAnswerCorrect = correctAnswer.contains(options.indexOf(selectedOption))
        }
    }

    fun continueToNext() {
        onQuestionCompleted(isAnswerCorrect)
        selectedOption = null
        answerChecked = false
        isAnswerCorrect = false
    }
}
