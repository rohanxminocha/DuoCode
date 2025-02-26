package com.uw.duocode.ui.screens.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    var selectedTab by mutableStateOf(0)
        private set

    val tabs = listOf("Quest Map", "Achievements", "Profile")
    val icons = listOf(
        Icons.Default.LocationOn,
        Icons.Default.Star,
        Icons.Default.AccountCircle
    )

    fun onTabSelected(index: Int) {
        selectedTab = index
    }
}
