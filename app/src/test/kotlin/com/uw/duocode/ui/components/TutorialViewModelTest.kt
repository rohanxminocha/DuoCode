package com.uw.duocode.ui.components

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class TutorialViewModelTest {
    @Test
    fun checkShowTutorialTrue() {
        val tutorialViewModel = TutorialViewModel()
        tutorialViewModel.showTutorial()
        assertTrue(tutorialViewModel.showTutorial)
        tutorialViewModel.showTutorial(true)
        assertTrue(tutorialViewModel.isAfterSignup)
    }

    @Test
    fun checkDismissTutorialFalse() {
        val tutorialViewModel = TutorialViewModel()
        tutorialViewModel.showTutorial()
        tutorialViewModel.dismissTutorial()
        assertFalse(tutorialViewModel.showTutorial)
    }

    @Test
    fun checkResetIsAfterSignup() {
        val tutorialViewModel = TutorialViewModel()
        tutorialViewModel.showTutorial(true)
        tutorialViewModel.resetAfterSignup()
        assertFalse(tutorialViewModel.isAfterSignup)
    }

    @Test
    fun checkNumberOfSlides() {
        val tutorialViewModel = TutorialViewModel()
        assertEquals(6, tutorialViewModel.tutorialSlides.size)
    }

    @Test
    fun checkTutorialSlidesTitles() {
        val tutorialViewModel = TutorialViewModel()
        assertEquals("Challenge Yourself!", tutorialViewModel.tutorialSlides[0].title)
        assertEquals("Sweet Success!", tutorialViewModel.tutorialSlides[1].title)
        assertEquals("Oops, Try Again!", tutorialViewModel.tutorialSlides[2].title)
        assertEquals("Climb the Ranks!", tutorialViewModel.tutorialSlides[3].title)
        assertEquals("Daily Challenges!", tutorialViewModel.tutorialSlides[4].title)
        assertEquals("Connect with Friends!", tutorialViewModel.tutorialSlides[5].title)
    }
} 