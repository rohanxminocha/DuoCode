package com.uw.duocode.ui.screens.questions

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MultipleChoiceViewModelTest {
    
    private lateinit var viewModel: MultipleChoiceViewModel
    private val questionText = "Which of the following are valid data structures?"
    private val options = listOf("Array", "Fosr Loop", "HashMap", "If Statement")
    private val correctAnswer = listOf(0, 2)
    private var questionCompletedCalled = false
    private val progress = 0.5f
    
    @Before
    fun setup() {
        questionCompletedCalled = false
        viewModel = MultipleChoiceViewModel(
            questionText = questionText,
            options = options,
            correctAnswer = correctAnswer,
            onQuestionCompleted = { questionCompletedCalled = true },
            progress = progress
        )
    }
    
    @Test
    fun testInitialState() {
        assertEquals(questionText, viewModel.questionText)
        assertEquals(options, viewModel.options)
        assertEquals(correctAnswer, viewModel.correctAnswer)
        assertEquals(progress, viewModel.progress)
        assertNull(viewModel.selectedOption)
        assertFalse(viewModel.answerChecked)
        assertFalse(viewModel.isAnswerCorrect)
    }
    
    @Test
    fun testOptionSelection() {
        assertNull(viewModel.selectedOption)
        
        viewModel.onOptionSelected("Array")
        assertEquals("Array", viewModel.selectedOption)
        
        viewModel.onOptionSelected("HashMap")
        assertEquals("HashMap", viewModel.selectedOption)
    }
    
    @Test
    fun testCheckCorrectAnswer() {
        viewModel.onOptionSelected("Array")
        
        viewModel.checkAnswer()
        
        assertTrue(viewModel.answerChecked)
        assertTrue(viewModel.isAnswerCorrect)
    }
    
    @Test
    fun testCheckIncorrectAnswer() {
        viewModel.onOptionSelected("For Loop")
        
        viewModel.checkAnswer()
        
        assertTrue(viewModel.answerChecked)
        assertFalse(viewModel.isAnswerCorrect)
    }
    
    @Test
    fun testContinueToNext() {
        viewModel.onOptionSelected("Array")
        viewModel.checkAnswer()
        
        viewModel.continueToNext()
        
        assertNull(viewModel.selectedOption)
        assertFalse(viewModel.answerChecked)
        assertFalse(viewModel.isAnswerCorrect)
        assertTrue(questionCompletedCalled)
    }
    
    @Test
    fun testCannotChangeSelectionAfterChecking() {
        viewModel.onOptionSelected("Array")
        
        viewModel.checkAnswer()
        
        viewModel.onOptionSelected("HashMap")
        
        assertEquals("Array", viewModel.selectedOption)
    }
} 