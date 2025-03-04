package com.uw.duocode.ui.screens.questions

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DragDropViewModelTest {
    
    private lateinit var viewModel: DragDropViewModel
    private val questionText = "Arrange the following steps of binary search algorithm in the correct order"
    private val initialSteps = listOf(
        "Find the middle element of the array",
        "Compare the middle element with the target value",
        "If target is greater, search in the right half",
        "If target is smaller, search in the left half",
        "Return the index if found, or -1 if not found"
    )
    private var questionCompletedCalled = false
    private val progress = 0.5f
    
    @Before
    fun setup() {
        questionCompletedCalled = false
        viewModel = DragDropViewModel(
            questionText = questionText,
            initialSteps = initialSteps,
            onQuestionCompleted = { questionCompletedCalled = true },
            progress = progress
        )
    }
    
    @Test
    fun testInitialState() {
        assertEquals(questionText, viewModel.questionText)
        assertEquals(progress, viewModel.progress)
        assertEquals(initialSteps.size, viewModel.steps.size)
        assertFalse(viewModel.answerChecked)
        assertFalse(viewModel.isAnswerCorrect)
        
        for (step in initialSteps) {
            assertTrue(viewModel.steps.contains(step))
        }
    }
    
    @Test
    fun testSwapSteps() {
        val initialOrder = viewModel.steps.toList()
        
        viewModel.onSwapSteps(0, 2)
        
        assertEquals(initialOrder[1], viewModel.steps[0])
        assertEquals(initialOrder[2], viewModel.steps[1])
        assertEquals(initialOrder[0], viewModel.steps[2])
        assertEquals(initialOrder[3], viewModel.steps[3])
        assertEquals(initialOrder[4], viewModel.steps[4])
    }
    
    @Test
    fun testSwapStepsToEnd() {
        val initialOrder = viewModel.steps.toList()
        
        viewModel.onSwapSteps(0, 4)
        
        assertEquals(initialOrder[1], viewModel.steps[0])
        assertEquals(initialOrder[2], viewModel.steps[1])
        assertEquals(initialOrder[3], viewModel.steps[2])
        assertEquals(initialOrder[4], viewModel.steps[3])
        assertEquals(initialOrder[0], viewModel.steps[4])
    }
    
    @Test
    fun testCheckAnswer() {
        assertFalse(viewModel.answerChecked)
        
        viewModel.checkAnswer()
        
        assertTrue(viewModel.answerChecked)
        
    }
    
    @Test
    fun testCannotSwapAfterChecking() {
        val initialOrder = viewModel.steps.toList()
        
        viewModel.checkAnswer()
        
        viewModel.onSwapSteps(0, 2)
        
        assertEquals(initialOrder, viewModel.steps)
    }
    
    @Test
    fun testContinueToNext() {
        viewModel.checkAnswer()
        
        viewModel.continueToNext()
        
        assertEquals(emptyList<String>(), viewModel.steps)
        assertFalse(viewModel.answerChecked)
        assertFalse(viewModel.isAnswerCorrect)
        assertTrue(questionCompletedCalled)
    }
} 