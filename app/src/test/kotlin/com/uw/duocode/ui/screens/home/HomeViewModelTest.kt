package com.uw.duocode.ui.screens.home

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        viewModel = HomeViewModel()
    }

    @Test
    fun `initial state has first tab selected`() {
        assertEquals(0, viewModel.selectedTab)
    }

    @Test
    fun `onTabSelected updates selectedTab value`() {
        viewModel.onTabSelected(1)
        assertEquals(1, viewModel.selectedTab)

        viewModel.onTabSelected(2)
        assertEquals(2, viewModel.selectedTab)

        viewModel.onTabSelected(0)
        assertEquals(0, viewModel.selectedTab)
    }

    @Test
    fun `tabs list contains expected values`() {
        val expectedTabs = listOf("Quest Map", "Challenges", "Profile")
        assertEquals(expectedTabs, viewModel.tabs)
    }

    @Test
    fun `tabs and icons lists have matching sizes`() {
        assertEquals(viewModel.tabs.size, viewModel.icons.size)
    }
}
