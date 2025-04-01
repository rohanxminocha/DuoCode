package com.uw.duocode.ui.screens.questions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel


data class NamedPair(val index: Int, val item: String)
data class PairSelectedItem(val index: Int, val item: String, val isKey: Boolean)

class MatchViewModel(
    val questionText: String,
    val correctPairs: Map<String, String>,
    private val onQuestionCompleted: (Boolean) -> Unit,
    val progress: Float
) : ViewModel() {
    private val matchColors = listOf(
        Color(0xFFFF9800), // Orange
        Color(0xFFFFEB3B), // Yellow
        Color(0xFF03A9F4), // Light Blue
        Color(0xFFE91E63), // Pink
        Color(0xFF00BCD4), // Cyan
        Color(0xFFFF5722), // Deep Orange
        Color(0xFF8BC34A)  // Light Green
    )

    private val pairColorMap = mutableMapOf<Pair<Int, Int>, Color>()
    private var currentColorIndex = 0
    private val tempSelectionColorMap = mutableMapOf<Int, Color>()

    // shuffle match options
    val shuffledKeys =
        correctPairs.keys.mapIndexed { index, item -> NamedPair(index, item) }.shuffled()
    val shuffledValues =
        correctPairs.values.mapIndexed { index, item -> NamedPair(index, item) }.shuffled()

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
    val allMatchesMade: Boolean
        get() = correctKeys.size == shuffledKeys.size && correctValues.size == shuffledValues.size

    fun getItemColor(index: Int, isKey: Boolean): Color? {
        if (selectedItem?.index == index && selectedItem?.isKey == isKey) {
            return tempSelectionColorMap[index]
        }
        val matchedPair = pairColorMap.keys.find { pair ->
            (pair.first == index && isKey) || (pair.second == index && !isKey)
        }
        return matchedPair?.let { pairColorMap[it] }
    }

    fun onItemClicked(namedItem: NamedPair, currentIsKey: Boolean) {
        if (answerChecked) return

        val index = namedItem.index
        val currentItem = namedItem.item

        if ((correctKeys.contains(index) && currentIsKey) || (correctValues.contains(index) && !currentIsKey)) return

        when {
            // no item selected yet
            selectedItem == null -> {
                val nextColor = matchColors[currentColorIndex % matchColors.size]
                tempSelectionColorMap[index] = nextColor
                selectedItem = PairSelectedItem(index, currentItem, currentIsKey)
            }
            // deselect if tapping the same item
            selectedItem == PairSelectedItem(index, currentItem, currentIsKey) -> {
                tempSelectionColorMap.remove(index)
                selectedItem = null
            }
            // different column selection; check for a match
            else -> {
                val previous = selectedItem!!
                if (previous.isKey == currentIsKey) {
                    // selections from the same column: update selection
                    tempSelectionColorMap.remove(previous.index)
                    val nextColor = matchColors[currentColorIndex % matchColors.size]
                    tempSelectionColorMap[index] = nextColor
                    selectedItem = PairSelectedItem(index, currentItem, currentIsKey)
                } else {
                    val matches = checkIsMatch(previous, namedItem, currentIsKey)
                    if (matches) {
                        // correct match: mark both items as correct
                        correctKeys += if (previous.isKey) previous.index else index
                        correctValues += if (previous.isKey) index else previous.index
                        val keyIndex = if (previous.isKey) previous.index else index
                        val valueIndex = if (previous.isKey) index else previous.index
                        val matchColor = tempSelectionColorMap[previous.index]
                            ?: matchColors[currentColorIndex % matchColors.size]
                        pairColorMap[Pair(keyIndex, valueIndex)] = matchColor
                        tempSelectionColorMap.remove(previous.index)
                        currentColorIndex++
                        selectedItem = null
                    } else {
                        // incorrect match: lock the question so the user must proceed
                        showErrorDialog = true
                        answerChecked = true
                        isAnswerCorrect = false
                        tempSelectionColorMap.remove(previous.index)
                        selectedItem = null
                    }
                }
            }
        }
    }

    private fun checkIsMatch(
        previous: PairSelectedItem,
        current: NamedPair,
        currentIsKey: Boolean
    ): Boolean {
        return if (previous.isKey && !currentIsKey) {
            correctPairs[previous.item] == current.item
        } else if (!previous.isKey && currentIsKey) {
            correctPairs[current.item] == previous.item
        } else {
            false
        }
    }

    fun checkAnswer() {
        answerChecked = true
        isAnswerCorrect = allMatchesMade
    }

    fun continueToNext() {
        if (allMatchesMade) {
            isAnswerCorrect = true
        }
        onQuestionCompleted(isAnswerCorrect)
        selectedItem = null
        answerChecked = false
        isAnswerCorrect = false
    }
}
