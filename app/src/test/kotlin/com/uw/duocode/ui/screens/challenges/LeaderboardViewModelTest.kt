package com.uw.duocode.ui.screens.challenges

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

class LeaderboardViewModelTest {

    private lateinit var viewModel: LeaderboardViewModel

    @Mock
    private lateinit var mockFirestore: FirebaseFirestore

    @Mock
    private lateinit var mockAuth: FirebaseAuth

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = LeaderboardViewModel(mockAuth, mockFirestore)
    }

    @Test
    fun `initial state has empty leaderboards and no errors`() {
        assertTrue(viewModel.globalLeaderboard.isEmpty())
        assertTrue(viewModel.friendsLeaderboard.isEmpty())
        assertFalse(viewModel.isLoading)
        assertNull(viewModel.errorMessage)
        assertNull(viewModel.currentUserRank)
        assertNull(viewModel.currentUserFriendsRank)
    }

    @Test
    fun `clearError sets error message to null`() {
        viewModel.setErrorForTesting("Test error message")

        assertEquals("Test error message", viewModel.errorMessage)

        viewModel.clearError()

        assertNull(viewModel.errorMessage)
    }

    @Test
    fun `updateLeaderboardForTesting correctly sets leaderboard data`() {
        val testGlobalEntries = listOf(
            LeaderboardEntry(
                userId = "user1",
                name = "User 1",
                email = "user1@example.com",
                questionsCompletedToday = 20,
                isCurrentUser = true
            ),
            LeaderboardEntry(
                userId = "user2",
                name = "User 2",
                email = "user2@example.com",
                questionsCompletedToday = 15,
                isCurrentUser = false
            )
        )

        val testFriendsEntries = listOf(
            LeaderboardEntry(
                userId = "user1",
                name = "User 1",
                email = "user1@example.com",
                questionsCompletedToday = 20,
                isCurrentUser = true
            ),
            LeaderboardEntry(
                userId = "user3",
                name = "Friend 1",
                email = "friend1@example.com",
                questionsCompletedToday = 10,
                isCurrentUser = false
            )
        )

        viewModel.updateLeaderboardForTesting(testGlobalEntries, testFriendsEntries)

        assertEquals(testGlobalEntries, viewModel.globalLeaderboard)
        assertEquals(testFriendsEntries, viewModel.friendsLeaderboard)

        assertEquals("User 1", viewModel.globalLeaderboard[0].name)
        assertEquals("User 2", viewModel.globalLeaderboard[1].name)

        assertEquals("User 1", viewModel.friendsLeaderboard[0].name)
        assertEquals("Friend 1", viewModel.friendsLeaderboard[1].name)

        assertEquals(20, viewModel.globalLeaderboard[0].questionsCompletedToday)
        assertEquals(10, viewModel.friendsLeaderboard[1].questionsCompletedToday)
    }
}
