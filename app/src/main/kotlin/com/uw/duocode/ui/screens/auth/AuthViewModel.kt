package com.uw.duocode.ui.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {

    var isLogin by mutableStateOf(true)
        private set

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var name by mutableStateOf("")

    fun toggleAuthMode() {
        isLogin = !isLogin
    }

    fun authenticate(
        onSuccess: () -> Unit,
        onMessage: (String) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        if (isLogin) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    onMessage("Successfully logged in")
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    onMessage(e.localizedMessage ?: "Login failed")
                }
        } else {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    // Update display name after successful sign-up
                    val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
                        displayName = name
                    }
                    auth.currentUser?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                onMessage("Successfully created account")
                            }
                        }
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    onMessage(e.localizedMessage ?: "Sign up failed")
                }
        }
    }

    fun sendResetPassword(onMessage: (String) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        if (email.isNotBlank()) {
            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener { onMessage("Password reset email sent") }
                .addOnFailureListener { e ->
                    onMessage(e.localizedMessage ?: "Failed to send reset email")
                }
        } else {
            onMessage("Please enter your email first")
        }
    }
}
