package com.uw.duocode.ui.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.uw.duocode.data.model.Friend
import com.uw.duocode.data.model.FriendRequest
import com.uw.duocode.data.model.FriendRequestStatus
import com.uw.duocode.data.model.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FriendViewModel : ViewModel() {
    
    var friends by mutableStateOf<List<Friend>>(emptyList())
        private set
    
    var pendingRequests by mutableStateOf<List<FriendRequest>>(emptyList())
        private set
    
    var isLoading by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    var searchEmail by mutableStateOf("")
    
    var searchResults by mutableStateOf<List<User>>(emptyList())
        private set
    
    var isSearching by mutableStateOf(false)
        private set
    
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    
    init {
        loadFriends()
        loadPendingRequests()
    }
    
    fun loadFriends() {
        val currentUser = auth.currentUser ?: return
        
        isLoading = true
        errorMessage = null
        
        viewModelScope.launch {
            try {
                val friendsCollection = db.collection("friends")
                    .whereEqualTo("userId", currentUser.uid)
                    .get()
                    .await()
                
                friends = friendsCollection.documents.mapNotNull { doc ->
                    doc.toObject(Friend::class.java)
                }
            } catch (e: Exception) {
                errorMessage = "Failed to load friends: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
    
    fun loadPendingRequests() {
        val currentUser = auth.currentUser ?: return
        
        isLoading = true
        errorMessage = null
        
        viewModelScope.launch {
            try {
                val requestsCollection = db.collection("friendRequests")
                    .whereEqualTo("receiverId", currentUser.uid)
                    .whereEqualTo("status", FriendRequestStatus.PENDING.name)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()
                
                pendingRequests = requestsCollection.documents.mapNotNull { doc ->
                    doc.toObject(FriendRequest::class.java)
                }
            } catch (e: Exception) {
                errorMessage = "Failed to load friend requests: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
    
    fun searchUserByEmail() {
        if (searchEmail.isBlank()) {
            searchResults = emptyList()
            return
        }
        
        isSearching = true
        errorMessage = null
        
        viewModelScope.launch {
            try {
                val usersCollection = db.collection("users")
                    .whereEqualTo("email", searchEmail)
                    .get()
                    .await()
                
                searchResults = usersCollection.documents.mapNotNull { doc ->
                    doc.toObject(User::class.java)
                }
            } catch (e: Exception) {
                errorMessage = "Failed to search user: ${e.localizedMessage}"
            } finally {
                isSearching = false
            }
        }
    }
    
    fun sendFriendRequest(receiverUser: User, onSuccess: () -> Unit) {
        val currentUser = auth.currentUser ?: return
        
        isLoading = true
        errorMessage = null
        
        viewModelScope.launch {
            try {
                val existingRequests = db.collection("friendRequests")
                    .whereEqualTo("senderId", currentUser.uid)
                    .whereEqualTo("receiverId", receiverUser.userUUID)
                    .get()
                    .await()
                
                if (!existingRequests.isEmpty) {
                    errorMessage = "A friend request to this user already exists"
                    isLoading = false
                    return@launch
                }
                
                val existingFriends = db.collection("friends")
                    .whereEqualTo("userId", currentUser.uid)
                    .whereEqualTo("friendId", receiverUser.userUUID)
                    .get()
                    .await()
                
                if (!existingFriends.isEmpty) {
                    errorMessage = "You are already friends with this user"
                    isLoading = false
                    return@launch
                }
                
                val currentUserDoc = db.collection("users")
                    .document(currentUser.uid)
                    .get()
                    .await()
                
                val currentUserObj = currentUserDoc.toObject(User::class.java)
                    ?: throw Exception("Failed to get current user details")
                
                val friendRequest = FriendRequest(
                    senderId = currentUser.uid,
                    senderName = currentUserObj.userId,
                    senderEmail = currentUser.email ?: "",
                    senderProfilePictureUrl = currentUserObj.profilePictureUrl,
                    receiverId = receiverUser.userUUID,
                    receiverName = receiverUser.userId,
                    receiverEmail = receiverUser.email
                )
                
                db.collection("friendRequests")
                    .add(friendRequest)
                    .await()
                
                onSuccess()
            } catch (e: Exception) {
                errorMessage = "Failed to send friend request: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
    
    fun acceptFriendRequest(request: FriendRequest, onSuccess: () -> Unit) {
        val currentUser = auth.currentUser ?: return
        
        isLoading = true
        errorMessage = null
        
        viewModelScope.launch {
            try {
                val requestQuery = db.collection("friendRequests")
                    .whereEqualTo("senderId", request.senderId)
                    .whereEqualTo("receiverId", currentUser.uid)
                    .whereEqualTo("status", FriendRequestStatus.PENDING.name)
                    .get()
                    .await()
                
                if (requestQuery.isEmpty) {
                    errorMessage = "Friend request not found"
                    isLoading = false
                    return@launch
                }
                
                val requestDoc = requestQuery.documents.first()
                
                requestDoc.reference.update("status", FriendRequestStatus.ACCEPTED.name).await()
                
                val friend1 = Friend(
                    userId = currentUser.uid,
                    friendId = request.senderId,
                    friendName = request.senderName,
                    friendEmail = request.senderEmail,
                    friendProfilePictureUrl = request.senderProfilePictureUrl
                )
                
                val friend2 = Friend(
                    userId = request.senderId,
                    friendId = currentUser.uid,
                    friendName = request.receiverName,
                    friendEmail = request.receiverEmail,
                    friendProfilePictureUrl = null // We don't have this info in the request
                )
                
                db.collection("friends").add(friend1).await()
                db.collection("friends").add(friend2).await()
                
                loadFriends()
                loadPendingRequests()
                
                onSuccess()
            } catch (e: Exception) {
                errorMessage = "Failed to accept friend request: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
    
    fun rejectFriendRequest(request: FriendRequest, onSuccess: () -> Unit) {
        val currentUser = auth.currentUser ?: return
        
        isLoading = true
        errorMessage = null
        
        viewModelScope.launch {
            try {
                val requestQuery = db.collection("friendRequests")
                    .whereEqualTo("senderId", request.senderId)
                    .whereEqualTo("receiverId", currentUser.uid)
                    .whereEqualTo("status", FriendRequestStatus.PENDING.name)
                    .get()
                    .await()
                
                if (requestQuery.isEmpty) {
                    errorMessage = "Friend request not found"
                    isLoading = false
                    return@launch
                }
                
                val requestDoc = requestQuery.documents.first()
                
                requestDoc.reference.update("status", FriendRequestStatus.REJECTED.name).await()
                
                loadPendingRequests()
                
                onSuccess()
            } catch (e: Exception) {
                errorMessage = "Failed to reject friend request: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
    
    fun clearError() {
        errorMessage = null
    }
    
    fun clearSearchResults() {
        searchResults = emptyList()
    }
} 