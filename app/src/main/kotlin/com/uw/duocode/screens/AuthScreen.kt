package com.uw.duocode.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.uw.duocode.navigation.DASHBOARD

@Composable
fun AuthScreen(navController: NavHostController) {
    var isLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()


        Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
            Text(
                text = if (isLogin) "Login" else "Sign Up",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (!isLogin) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            val context = LocalContext.current

            fun onShowMessage(message: String) {
                println(message)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }

            Button(
                onClick = {
                    if (isLogin) {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener {
                                onShowMessage("Successfully logged in")
                                navController.navigate(DASHBOARD)
                            }
                            .addOnFailureListener { e ->
                                onShowMessage(e.localizedMessage ?: "Login failed")
                            }
                    } else {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener {
                                val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
                                    displayName = name
                                }
                                auth.currentUser?.updateProfile(profileUpdates)
                                    ?.addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            onShowMessage("Successfully created account")
                                        }
                                    }
                                navController.navigate(DASHBOARD)
                            }
                            .addOnFailureListener { e ->
                                onShowMessage(e.localizedMessage ?: "Sign up failed")
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isLogin) "Login" else "Sign Up")
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { isLogin = !isLogin }
            ) {
                Text(if (isLogin) "Need an account? Sign Up" else "Have an account? Login")
            }

            if (isLogin) {
                TextButton(
                    onClick = {
                        if (email.isNotBlank()) {
                            auth.sendPasswordResetEmail(email)
                                .addOnSuccessListener {
                                    onShowMessage("Password reset email sent")
                                }
                                .addOnFailureListener { e ->
                                    onShowMessage(e.localizedMessage ?: "Failed to send reset email")
                                }
                        } else {
                            onShowMessage("Please enter your email first")
                        }
                    }
                ) {
                    Text("Forgot Password?")
                }
            }
    }
}