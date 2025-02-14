package com.uw.duocode.ui.screens.questions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class DragDropViewModel : ViewModel() {

    val questionText = "Drag these steps to order them correctly for the Merge Sort algorithm."

    var steps by mutableStateOf(
        listOf(
            "Sort each half recursively",
            "Split the list into two halves",
            "Merge the two sorted halves"
        )
    )
        private set

    private val correctOrder = listOf(
        "Split the list into two halves",
        "Sort each half recursively",
        "Merge the two sorted halves"
    )

    var answerChecked by mutableStateOf(false)
        private set

    var isAnswerCorrect by mutableStateOf(false)
        private set

    fun onSwapSteps(fromIndex: Int, toIndex: Int) {
        steps = steps.toMutableList().apply {
            add(toIndex, removeAt(fromIndex))
        }
    }

    fun checkAnswer() {
        if (!answerChecked) {
            answerChecked = true
            isAnswerCorrect = steps == correctOrder
        }
    }

    fun continueToNext() {
        steps = listOf(
            "Sort each half recursively",
            "Split the list into two halves",
            "Merge the two sorted halves"
        )
        answerChecked = false
        isAnswerCorrect = false
    }
}