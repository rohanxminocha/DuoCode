package com.uw.duocode.ui.screens.questions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MatchViewModel(val questionText: String,
    val correctPairs: Map<String, String>,
    private val onQuestionCompleted: () -> Unit, val progress: Float) : ViewModel() {

    // shuffle match options
    private val shuffledItems1 = correctPairs.keys.shuffled()
    private val shuffledItems2 = correctPairs.values.shuffled()

    val items: List<String> = shuffledItems1.zip(shuffledItems2)
        .flatMap { (item1, item2) -> listOf(item1, item2) }

    // state variables:
    var selectedItem by mutableStateOf<String?>(null)
        private set
    var correctMatches by mutableStateOf(setOf<String>())
        private set
    var showErrorDialog by mutableStateOf(false)
        private set

    // continue button gets enabled when all items have been matched
    val allMatchesCorrect: Boolean get() = correctMatches.size == items.size

    // called when an item is clicked
    fun onItemClicked(item: String) {
        if (correctMatches.contains(item)) return

        when {
            // no item selected yet
            selectedItem == null -> {
                selectedItem = item
            }
            // double-clicking item unselects it
            selectedItem == item -> {
                selectedItem = null
            }
            // check for existing selection
            else -> {
                val first = selectedItem!!
                val firstIndex = items.indexOf(first)
                val secondIndex = items.indexOf(item)

                // if both items belong to same column, switch selection
                if (firstIndex % 2 == secondIndex % 2) {
                    selectedItem = item
                } else {
                    // if items from different columns, check match
                    if (correctPairs[first] == item || correctPairs[item] == first) {
                        // valid match
                        correctMatches = correctMatches + setOf(first, item)
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

    fun continueToNext() {
        onQuestionCompleted()
    }
}
