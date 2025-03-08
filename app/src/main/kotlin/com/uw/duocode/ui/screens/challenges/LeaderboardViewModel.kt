package com.uw.duocode.ui.screens.challenges

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.uw.duocode.data.model.Friend
import com.uw.duocode.data.model.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class LeaderboardEntry(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val profilePictureUrl: String? = null,
    val questionsCompletedToday: Int = 0,
    val isCurrentUser: Boolean = false
)

class LeaderboardViewModel : ViewModel() {
    
    var globalLeaderboard by mutableStateOf<List<LeaderboardEntry>>(emptyList())
        private set
    
    var friendsLeaderboard by mutableStateOf<List<LeaderboardEntry>>(emptyList())
        private set
    
    var isLoading by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    var currentUserRank by mutableStateOf<Int?>(null)
        private set
    
    var currentUserFriendsRank by mutableStateOf<Int?>(null)
        private set
    
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    
    fun loadLeaderboards() {
        isLoading = true
        errorMessage = null
        
        viewModelScope.launch {
            try {
                val globalLeaderboardSnapshot = db.collection("users")
                    .orderBy("questionsCompletedToday", Query.Direction.DESCENDING)
                    .limit(50)
                    .get()
                    .await()
                
                val currentUserId = auth.currentUser?.uid ?: ""
                
                val globalEntries = globalLeaderboardSnapshot.documents.mapIndexed { index, doc ->
                    val user = doc.toObject(User::class.java)
                    val isCurrentUser = doc.id == currentUserId
                    
                    if (isCurrentUser) {
                        currentUserRank = index + 1
                    }
                    
                    LeaderboardEntry(
                        userId = doc.id,
                        name = user?.userId ?: "Unknown",
                        email = user?.email ?: "",
                        profilePictureUrl = user?.profilePictureUrl,
                        questionsCompletedToday = user?.questionsCompletedToday ?: 0,
                        isCurrentUser = isCurrentUser
                    )
                }
                
                globalLeaderboard = globalEntries
                
                loadFriendsLeaderboard()
                
            } catch (e: Exception) {
                errorMessage = "Failed to load leaderboard: ${e.localizedMessage}"
                isLoading = false
            }
        }
    }
    
    private suspend fun loadFriendsLeaderboard() {
        try {
            val currentUserId = auth.currentUser?.uid ?: return
            
            val friendsSnapshot = db.collection("friends")
                .whereEqualTo("userId", currentUserId)
                .get()
                .await()
            
            val friends = friendsSnapshot.documents.mapNotNull { doc ->
                doc.toObject(Friend::class.java)
            }
            
            val currentUserDoc = db.collection("users")
                .document(currentUserId)
                .get()
                .await()
            
            val currentUser = currentUserDoc.toObject(User::class.java)
            
            val allEntries = mutableListOf<LeaderboardEntry>()
            
            if (currentUser != null) {
                allEntries.add(
                    LeaderboardEntry(
                        userId = currentUserId,
                        name = currentUser.userId,
                        email = currentUser.email,
                        profilePictureUrl = currentUser.profilePictureUrl,
                        questionsCompletedToday = currentUser.questionsCompletedToday,
                        isCurrentUser = true
                    )
                )
            }
            
            for (friend in friends) {
                val friendDoc = db.collection("users")
                    .document(friend.friendId)
                    .get()
                    .await()
                
                val friendUser = friendDoc.toObject(User::class.java)
                
                if (friendUser != null) {
                    allEntries.add(
                        LeaderboardEntry(
                            userId = friend.friendId,
                            name = friendUser.userId,
                            email = friendUser.email,
                            profilePictureUrl = friendUser.profilePictureUrl,
                            questionsCompletedToday = friendUser.questionsCompletedToday,
                            isCurrentUser = false
                        )
                    )
                }
            }
            
            val sortedEntries = allEntries.sortedByDescending { it.questionsCompletedToday }
            
            currentUserFriendsRank = sortedEntries.indexOfFirst { it.isCurrentUser } + 1
            
            friendsLeaderboard = sortedEntries
            
        } catch (e: Exception) {
            errorMessage = "Failed to load friends leaderboard: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }
    
    fun clearError() {
        errorMessage = null
    }
} 