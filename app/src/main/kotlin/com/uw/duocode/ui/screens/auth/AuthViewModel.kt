package com.uw.duocode.ui.screens.auth

import android.content.Context
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

class AuthViewModel : ViewModel() {

    var isLogin by mutableStateOf(true)
        private set

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var name by mutableStateOf("")
    var isLoading by mutableStateOf(false)
        private set

    fun toggleAuthMode() {
        isLogin = !isLogin
    }

    fun authenticate(
        context: Context,
        onSuccess: () -> Unit,
        onMessage: (String) -> Unit
    ) {
        isLoading = true
        val auth = FirebaseAuth.getInstance()
        if (isLogin) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    isLoading = false
                    onMessage("Successfully logged in")
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    isLoading = false
                    onMessage(e.localizedMessage ?: "Login failed")
                }
        } else {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    val user = authResult.user
                    if (user != null) {
                        // Update display name after successful sign-up
                        val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
                            displayName = name
                        }
                        
                        user.updateProfile(profileUpdates)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    viewModelScope.launch {
                                        try {
                                            val profilePictureUrl = ProfilePictureGenerator.generateAndUploadProfilePicture(
                                                context = context,
                                                userId = user.uid,
                                                name = name.ifEmpty { email }
                                            )
                                            
                                            createUserInFirestore(
                                                userId = user.uid,
                                                email = email,
                                                name = name,
                                                profilePictureUrl = profilePictureUrl
                                            )
                                            
                                            isLoading = false
                                            onMessage("Successfully created account")
                                            onSuccess()
                                        } catch (e: Exception) {
                                            createUserInFirestore(
                                                userId = user.uid,
                                                email = email,
                                                name = name,
                                                profilePictureUrl = null
                                            )
                                            
                                            isLoading = false
                                            onMessage("Account created but couldn't generate profile picture")
                                            onSuccess()
                                        }
                                    }
                                } else {
                                    isLoading = false
                                    onMessage("Account created but couldn't set display name")
                                    onSuccess()
                                }
                            }
                    } else {
                        isLoading = false
                        onMessage("Sign up successful but couldn't get user details")
                        onSuccess()
                    }
                }
                .addOnFailureListener { e ->
                    isLoading = false
                    onMessage(e.localizedMessage ?: "Sign up failed")
                }
        }
    }

    private fun createUserInFirestore(
        userId: String,
        email: String,
        name: String,
        profilePictureUrl: String?
    ) {
        val db = FirebaseFirestore.getInstance()
        val userCollection = db.collection("users")
        
        val newUser = User(
            userId = name,
            uid = userId,
            email = email,
            profilePictureUrl = profilePictureUrl
        )
        
        userCollection.add(newUser)
            .addOnSuccessListener { documentReference ->
                println("User document created with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                println("Error creating user document: ${e.message}")
            }
    }

    fun sendResetPassword(onMessage: (String) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        if (email.isNotBlank()) {
            isLoading = true
            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener { 
                    isLoading = false
                    onMessage("Password reset email sent") 
                }
                .addOnFailureListener { e ->
                    isLoading = false
                    onMessage(e.localizedMessage ?: "Failed to send reset email")
                }
        } else {
            onMessage("Please enter your email first")
        }
    }
}
