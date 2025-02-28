package com.uw.duocode.ui.screens.questions

import androidx.lifecycle.ViewModel
import com.uw.duocode.R

class ResultViewModel : ViewModel() {
    val title: String = "Congratulations \uD83C\uDF89"
    val message: String = "You got all the questions correct!"
    val imageResId: Int = R.drawable.great
}
