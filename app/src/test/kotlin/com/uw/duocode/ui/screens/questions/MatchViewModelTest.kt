package com.uw.duocode.ui.screens.questions

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MatchViewModelTest {
    
    private lateinit var viewModel: MatchViewModel
    private val questionText = "Match the data structures with their characteristics"
    private val correctPairs = mapOf(
        "Array" to "Contiguous memory allocation",
        "LinkedList" to "Non-contiguous memory allocation",
        "HashMap" to "Key-value pairs",
        "Stack" to "LIFO principle"
    )
    private var questionCompletedCalled = false
    private val progress = 0.5f
    
    @Before
    fun setup() {
        questionCompletedCalled = false
        viewModel = MatchViewModel(
            questionText = questionText,
            correctPairs = correctPairs,
            onQuestionCompleted = { questionCompletedCalled = true },
            progress = progress
        )
    }
    
    @Test
    fun testInitialState() {
        assertEquals(questionText, viewModel.questionText)
        assertEquals(correctPairs, viewModel.correctPairs)
        assertEquals(progress, viewModel.progress)
        assertNull(viewModel.selectedItem)
        assertEquals(0, viewModel.correctKeys.size)
        assertEquals(0, viewModel.correctValues.size)
        assertFalse(viewModel.showErrorDialog)
        assertFalse(viewModel.allMatchesCorrect)
        
        assertEquals(4, viewModel.shuffledKeys.size)
        assertEquals(4, viewModel.shuffledValues.size)
        assertEquals(8, viewModel.items.size)
    }
    
    @Test
    fun testItemSelection() {
        val keyItem = viewModel.shuffledKeys.first()
        
        assertNull(viewModel.selectedItem)
        
        viewModel.onItemClicked(keyItem, true)
        
        assertEquals(keyItem.index, viewModel.selectedItem?.index)
        assertEquals(keyItem.item, viewModel.selectedItem?.item)
        assertTrue(viewModel.selectedItem?.isKey ?: false)
        
        viewModel.onItemClicked(keyItem, true)
        
        assertNull(viewModel.selectedItem)
    }
    
    @Test
    fun testCorrectMatch() {
        val keyItem = viewModel.shuffledKeys.first()
        val keyValue = keyItem.item
        
        val valueItem = viewModel.shuffledValues.find {
            it.item == correctPairs[keyValue] 
        }!!
        
        viewModel.onItemClicked(keyItem, true)
        
        viewModel.onItemClicked(valueItem, false)
        
        assertTrue(viewModel.correctKeys.contains(keyItem.index))
        assertTrue(viewModel.correctValues.contains(valueItem.index))
        assertNull(viewModel.selectedItem)
        
        assertFalse(viewModel.showErrorDialog)
    }

    @Test
    fun testSwitchSelection() {
        val keyItem1 = viewModel.shuffledKeys[0]
        val keyItem2 = viewModel.shuffledKeys[1]
        
        viewModel.onItemClicked(keyItem1, true)
        
        assertEquals(keyItem1.index, viewModel.selectedItem?.index)
        
        viewModel.onItemClicked(keyItem2, true)
        
        assertEquals(keyItem2.index, viewModel.selectedItem?.index)
    }
    
    @Test
    fun testAllMatchesCorrect() {
        assertFalse(viewModel.allMatchesCorrect)
        
        for (keyItem in viewModel.shuffledKeys) {
            val keyValue = keyItem.item
            val valueItem = viewModel.shuffledValues.find { 
                it.item == correctPairs[keyValue] 
            }!!
            
            viewModel.onItemClicked(keyItem, true)
            viewModel.onItemClicked(valueItem, false)
        }
        
        assertTrue(viewModel.allMatchesCorrect)
        assertEquals(viewModel.shuffledKeys.size, viewModel.correctKeys.size)
        assertEquals(viewModel.shuffledValues.size, viewModel.correctValues.size)
    }
    
    @Test
    fun testContinueToNext() {
        val keyItem = viewModel.shuffledKeys.first()
        val keyValue = keyItem.item
        val valueItem = viewModel.shuffledValues.find { 
            it.item == correctPairs[keyValue] 
        }!!
        
        viewModel.onItemClicked(keyItem, true)
        viewModel.onItemClicked(valueItem, false)
        
        viewModel.continueToNext()
        
        assertTrue(questionCompletedCalled)
    }
} 