package com.uw.duocode.ui.screens.profile

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.uw.duocode.data.model.User
import com.uw.duocode.ui.utils.ProfilePictureGenerator
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {
    
    var user by mutableStateOf<User?>(null)
        private set
    
    var isLoading by mutableStateOf(true)
        private set
    
    var isUpdatingProfilePicture by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    
    init {
        loadUserData()
    }
    
    /**
     * Gets the user document reference by querying for the document where uid equals the current user's UID
     */
    private suspend fun getUserDocRef(uid: String): DocumentReference? {
        val querySnapshot = db.collection("users").whereEqualTo("uid", uid).get().await()
        return if (!querySnapshot.isEmpty) {
            querySnapshot.documents.first().reference
        } else {
            null
        }
    }
    
    fun loadUserData() {
        val currentUser = auth.currentUser ?: return
        
        isLoading = true
        errorMessage = null
        
        viewModelScope.launch {
            try {
                // Query for the user document where uid equals the current user's UID
                val querySnapshot = db.collection("users").whereEqualTo("uid", currentUser.uid).get().await()
                if (!querySnapshot.isEmpty) {
                    user = querySnapshot.documents.first().toObject(User::class.java)
                } else {
                    errorMessage = "User document not found"
                }
            } catch (e: Exception) {
                errorMessage = "Failed to load user data: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
    
    fun updateProfilePicture(context: Context, imageUri: Uri, onSuccess: () -> Unit) {
        val currentUser = auth.currentUser ?: return
        
        isUpdatingProfilePicture = true
        errorMessage = null
        
        viewModelScope.launch {
            var profilePictureUrl = ""
            
            try {
                profilePictureUrl = ProfilePictureGenerator.uploadProfilePictureFromUri(
                    context = context,
                    userId = currentUser.uid,
                    imageUri = imageUri
                )
                
                // Get the user document reference
                val userDocRef = getUserDocRef(currentUser.uid)
                if (userDocRef != null) {
                    userDocRef.update("profilePictureUrl", profilePictureUrl).await()
                    user = user?.copy(profilePictureUrl = profilePictureUrl)
                    onSuccess()
                } else {
                    throw Exception("User document not found")
                }
            } catch (e: Exception) {
                errorMessage = "Failed to update profile picture: ${e.localizedMessage}"
            } finally {
                isUpdatingProfilePicture = false
            }
        }
    }
    
    fun generateNewProfilePicture(context: Context, onSuccess: () -> Unit) {
        val currentUser = auth.currentUser ?: return
        val displayName = currentUser.displayName ?: currentUser.email ?: return
        
        isUpdatingProfilePicture = true
        errorMessage = null
        
        viewModelScope.launch {
            var profilePictureUrl = ""
            
            try {
                profilePictureUrl = ProfilePictureGenerator.generateAndUploadProfilePicture(
                    context = context,
                    userId = currentUser.uid,
                    name = displayName
                )
                
                // Get the user document reference
                val userDocRef = getUserDocRef(currentUser.uid)
                if (userDocRef != null) {
                    userDocRef.update("profilePictureUrl", profilePictureUrl).await()
                    user = user?.copy(profilePictureUrl = profilePictureUrl)
                    onSuccess()
                } else {
                    throw Exception("User document not found")
                }
            } catch (e: Exception) {
                errorMessage = "Failed to generate new profile picture: ${e.localizedMessage}"
            } finally {
                isUpdatingProfilePicture = false
            }
        }
    }
    
    fun clearError() {
        errorMessage = null
    }
} 