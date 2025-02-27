package com.uw.duocode.ui.screens.questions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class DragDropViewModel(
    val questionText: String,
    initialSteps: List<String>,
    private val onQuestionCompleted: () -> Unit,
    val progress: Float
) : ViewModel() {

    var steps by mutableStateOf(
        initialSteps.shuffled()
    )
        private set

    private val correctOrder = initialSteps

    var answerChecked by mutableStateOf(false)
        private set

    var isAnswerCorrect by mutableStateOf(false)
        private set

    fun onSwapSteps(fromIndex: Int, toIndex: Int) {
        // Only allow swapping if the answer hasn't been checked.
        if (!answerChecked) {
            steps = steps.toMutableList().apply {
                add(toIndex, removeAt(fromIndex))
            }
        }
    }

    fun checkAnswer() {
        if (!answerChecked) {
            answerChecked = true
            isAnswerCorrect = steps == correctOrder
        }
    }

    fun continueToNext() {
        steps = listOf()
        answerChecked = false
        isAnswerCorrect = false
        onQuestionCompleted()
    }
}
