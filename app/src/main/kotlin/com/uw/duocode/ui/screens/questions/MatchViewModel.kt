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
    private val onQuestionCompleted: () -> Unit, val progress: Float) : ViewModel()
{

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

    // continue button gets enabled when all items have been matched
    val allMatchesCorrect: Boolean get() = correctKeys.size == shuffledKeys.size && correctValues.size == shuffledValues.size

    // called when an item is clicked
    fun onItemClicked(namedItem: NamedPair, currentIsKey: Boolean) {
        val index = namedItem.index
        val currentItem = namedItem.item

        if ((correctKeys.contains(index) && currentIsKey) || (correctValues.contains(index) && !currentIsKey) ) return
        when {
            // no item selected yet
            selectedItem == null -> {
                selectedItem = PairSelectedItem(index, currentItem,  currentIsKey)
            }
            // double-clicking item unselects it
            selectedItem == PairSelectedItem(index, currentItem, currentIsKey) -> {
                selectedItem = null
            }
            // check for existing selection
            else -> {
                val selected = selectedItem!!
                val selectedIndex = selected.index
                val currentIndex = index

                // if both items belong to same column, switch selection
                if (selected.isKey == currentIsKey) {
                    selectedItem = PairSelectedItem(currentIndex, currentItem, currentIsKey)
                } else {
                    // if items from different columns, check match
                    var matches: Boolean
                    // match again all possible options including duplicates
                    if (currentIsKey) {
                        val possibleMatchingValues = correctPairs.toList().withIndex().filter { kv -> kv.value.second == selected.item }
                        matches = possibleMatchingValues.map { kv -> kv.index }.contains(currentIndex)
                    } else {
                        val possibleMatchingKeys = correctPairs.toList().withIndex().filter { kv -> kv.value.second == correctPairs[selected.item] }
                        matches = possibleMatchingKeys.map { kv -> kv.index }.contains(currentIndex)
                    }
                    if (selectedIndex == currentIndex || matches) {
                        // valid match
                        correctKeys += if (selected.isKey) selected.index else index
                        correctValues += if (selected.isKey) index else selected.index
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
