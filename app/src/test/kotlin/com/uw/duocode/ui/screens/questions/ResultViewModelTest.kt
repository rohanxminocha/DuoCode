package com.uw.duocode.ui.screens.questions

import org.junit.Assert.assertEquals
import org.junit.Test

class ResultViewModelTest {

    @Test
    fun `test perfect score and correct time formatting`() {
        val viewModel = ResultViewModel(correctAnswerCount = 10, totalQuestions = 10, timeSpentSeconds = 125)
        assertEquals(1.0f, viewModel.percentage, 0.0001f)
        assertEquals("Good Job!", viewModel.title)
        assertEquals("You got 100% of the questions correct!", viewModel.message)
        assertEquals("Time Spent: 2:05", viewModel.timeMessage)
    }

    @Test
    fun `test score below threshold and message formatting`() {
        val viewModel = ResultViewModel(correctAnswerCount = 7, totalQuestions = 10, timeSpentSeconds = 30)
        assertEquals(0.7f, viewModel.percentage, 0.0001f)
        assertEquals("Try Again", viewModel.title)
        assertEquals("You got 70% of the questions correct. Keep practicing!", viewModel.message)
        assertEquals("Time Spent: 0:30", viewModel.timeMessage)
    }

    @Test
    fun `test boundary percentage equals 80 returns Good Job`() {
        val viewModel = ResultViewModel(correctAnswerCount = 8, totalQuestions = 10, timeSpentSeconds = 90)
        assertEquals(0.8f, viewModel.percentage, 0.0001f)
        assertEquals("Good Job!", viewModel.title)
        assertEquals("You got 80% of the questions correct!", viewModel.message)
        assertEquals("Time Spent: 1:30", viewModel.timeMessage)
    }

    @Test
    fun `test time formatting with single digit seconds`() {
        val viewModel = ResultViewModel(correctAnswerCount = 5, totalQuestions = 10, timeSpentSeconds = 61)
        assertEquals("Time Spent: 1:01", viewModel.timeMessage)
    }

    @Test
    fun `test zero time`() {
        val viewModel = ResultViewModel(correctAnswerCount = 3, totalQuestions = 10, timeSpentSeconds = 0)
        assertEquals("Time Spent: 0:00", viewModel.timeMessage)
    }
}
