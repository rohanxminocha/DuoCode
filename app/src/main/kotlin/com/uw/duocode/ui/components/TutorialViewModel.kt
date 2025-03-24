package com.uw.duocode.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.uw.duocode.R

class TutorialViewModel : ViewModel() {
    var showTutorial by mutableStateOf(false)
        private set
    
    var isAfterSignup by mutableStateOf(false)
        private set
    
    fun showTutorial(afterSignup: Boolean = false) {
        showTutorial = true
        isAfterSignup = afterSignup
    }
    
    fun dismissTutorial() {
        showTutorial = false
    }
    
    fun resetAfterSignup() {
        isAfterSignup = false
    }
    
    val tutorialSlides = listOf(
        TutorialSlide(
            imageRes = R.drawable.question,
            title = "Challenge Yourself!",
            description = "Tackle coding puzzles that range from easy-peasy to brain-busting. Every question is a step toward coding mastery!"
        ),
        TutorialSlide(
            imageRes = R.drawable.correct_question,
            title = "Sweet Success!",
            description = "When you nail the answer, your code shines bright green! Keep collecting those wins—they add up to real skills!"
        ),
        TutorialSlide(
            imageRes = R.drawable.incorrect_question,
            title = "Oops, Try Again!",
            description = "Don't sweat the mistakes—they're where the real learning happens! When you see red, it's just a puzzle piece that needs another look."
        ),
        TutorialSlide(
            imageRes = R.drawable.leaderboard,
            title = "Climb the Ranks!",
            description = "See your name in lights on our leaderboard! Compete with friends and coders worldwide to earn bragging rights as the ultimate code ninja!"
        ),
        TutorialSlide(
            imageRes = R.drawable.challenges,
            title = "Daily Challenges!",
            description = "Keep your streak alive with daily coding challenges! Build a habit of coding excellence one day at a time."
        ),
        TutorialSlide(
            imageRes = R.drawable.profile_n_friends,
            title = "Connect with Friends!",
            description = "Coding's more fun with friends! Add your buddies and track each other's progress as you level up together."
        )
    )
    
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                TutorialViewModel()
            }
        }
    }
} 