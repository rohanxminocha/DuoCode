package com.uw.duocode.ui.screens.questions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


data class NamedPair(val index: Int, val item: String)
data class PairSelectedItem(val index: Int, val item: String, val isKey: Boolean)

class MatchViewModel(
    val questionText: String,
    val correctPairs: Map<String, String>,
    private val onQuestionCompleted: (Boolean) -> Unit,
    val progress: Float
) : ViewModel() {

    // shuffle match options
    val shuffledKeys = correctPairs.keys.mapIndexed{ index, item -> NamedPair(index, item) }.shuffled()
    val shuffledValues = correctPairs.values.mapIndexed{ index, item -> NamedPair(index, item) }.shuffled()

    val items: List<NamedPair> = shuffledKeys.zip(shuffledValues)
        .flatMap { (item1, item2) -> listOf(item1, item2) }

    // state variables:
    var selectedItem by mutableStateOf<PairSelectedItem?>(null)
        private set

    var correctKeys by mutableStateOf(setOf<Int>())
        private set

    var correctValues by mutableStateOf(setOf<Int>())
        private set

    var showErrorDialog by mutableStateOf(false)
        private set

    // states to match the check/continue flow
    var answerChecked by mutableStateOf(false)
        private set
    var isAnswerCorrect by mutableStateOf(false)
        private set

    val allMatchesCorrect: Boolean
        get() = correctKeys.size == shuffledKeys.size && correctValues.size == shuffledValues.size

    fun onItemClicked(namedItem: NamedPair, currentIsKey: Boolean) {
        if (answerChecked) return

        val index = namedItem.index

        if ((correctKeys.contains(index) && currentIsKey) ||
            (correctValues.contains(index) && !currentIsKey)
        ) return

        when (selectedItem) {
            null -> {
                selectedItem = PairSelectedItem(index, namedItem.item, currentIsKey)
            }
            PairSelectedItem(index, namedItem.item, currentIsKey) -> {
                selectedItem = null
            }
            // check for existing selection
            else -> {
                val previous = selectedItem!!
                val previousIndex = previous.index

                if (previous.isKey == currentIsKey) {
                    selectedItem = PairSelectedItem(index, namedItem.item, currentIsKey)
                } else {
                    val matched = checkIsMatch(previous, namedItem, currentIsKey)
                    if (matched) {
                        correctKeys += if (previous.isKey) previousIndex else index
                        correctValues += if (previous.isKey) index else previousIndex
                        selectedItem = null
                    } else {
                        // invalid match: show error dialog
                        showErrorDialog = true
                        viewModelScope.launch {
                            selectedItem = null
                            delay(2000) // dismiss after 2s
                            showErrorDialog = false
                        }
                    }
                }
            }
        }
    }

    private fun checkIsMatch(previous: PairSelectedItem, current: NamedPair, currentIsKey: Boolean): Boolean {
        return if (currentIsKey) {
            val possibleMatches = correctPairs.entries.filter { it.value == previous.item }
            possibleMatches.any { it.key == current.item }
        } else {
            val correctValue = correctPairs[previous.item]
            correctValue == current.item
        }
    }

    fun checkAnswer() {
        answerChecked = true
        isAnswerCorrect = allMatchesCorrect
    }

    fun continueToNext() {
        onQuestionCompleted(isAnswerCorrect)
        selectedItem = null
        answerChecked = false
        isAnswerCorrect = false
    }
}
