package com.uw.duocode.ui.screens.questions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MultipleChoiceViewModel : ViewModel() {
    val questionText =
        "In a graph traversal, which traversal technique explores all neighbours of a node before moving on to the next node?"

    val options = listOf(
        "Depth-First Search (DFS)",
        "Breadth-First Search (BFS)",
        "Dijkstra’s Algorithm",
        "Topological Sort"
    )

    val correctAnswer = "Breadth-First Search (BFS)"

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
        if (selectedOption != null && !answerChecked) {
            answerChecked = true
            isAnswerCorrect = selectedOption == correctAnswer
        }
    }

    fun continueToNext() {
        selectedOption = null
        answerChecked = false
        isAnswerCorrect = false
    }
}
