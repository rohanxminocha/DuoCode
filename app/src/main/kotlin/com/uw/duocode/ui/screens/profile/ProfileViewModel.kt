package com.uw.duocode.ui.screens.profile

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
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
    
    fun loadUserData() {
        val currentUser = auth.currentUser ?: return
        
        isLoading = true
        errorMessage = null
        
        viewModelScope.launch {
            try {
                val userDoc = db.collection("users").document(currentUser.uid).get().await()
                if (userDoc.exists()) {
                    user = userDoc.toObject(User::class.java)
                } else {
                    // If user document doesn't exist, try querying by userUUID field
                    val querySnapshot = db.collection("users").whereEqualTo("uid", currentUser.uid).get().await()
                    if (!querySnapshot.isEmpty) {
                        user = querySnapshot.documents.first().toObject(User::class.java)
                    }
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
                

                val userRef = db.collection("users").document(currentUser.uid)
                userRef.update("profilePictureUrl", profilePictureUrl).await()
                
                user = user?.copy(profilePictureUrl = profilePictureUrl)
                
                onSuccess()
            } catch (e: Exception) {
                try {
                    if (profilePictureUrl.isNotEmpty()) {
                        val querySnapshot = db.collection("users").whereEqualTo("uid", currentUser.uid).get().await()
                        if (!querySnapshot.isEmpty) {
                            val docRef = querySnapshot.documents.first().reference
                            docRef.update("profilePictureUrl", profilePictureUrl).await()
                            
                            user = user?.copy(profilePictureUrl = profilePictureUrl)
                            
                            onSuccess()
                        } else {
                            throw Exception("User document not found")
                        }
                    } else {
                        throw Exception("Failed to upload profile picture")
                    }
                } catch (e2: Exception) {
                    errorMessage = "Failed to update profile picture: ${e2.localizedMessage}"
                }
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
                
                val userRef = db.collection("users").document(currentUser.uid)
                userRef.update("profilePictureUrl", profilePictureUrl).await()
                
                user = user?.copy(profilePictureUrl = profilePictureUrl)
                
                onSuccess()
            } catch (e: Exception) {
                try {
                    if (profilePictureUrl.isNotEmpty()) {
                        val querySnapshot = db.collection("users").whereEqualTo("uid", currentUser.uid).get().await()
                        if (!querySnapshot.isEmpty) {
                            val docRef = querySnapshot.documents.first().reference
                            docRef.update("profilePictureUrl", profilePictureUrl).await()
                            
                            user = user?.copy(profilePictureUrl = profilePictureUrl)
                            
                            onSuccess()
                        } else {
                            throw Exception("User document not found")
                        }
                    } else {
                        throw Exception("Failed to generate profile picture")
                    }
                } catch (e2: Exception) {
                    errorMessage = "Failed to generate new profile picture: ${e2.localizedMessage}"
                }
            } finally {
                isUpdatingProfilePicture = false
            }
        }
    }
    
    fun clearError() {
        errorMessage = null
    }
} 