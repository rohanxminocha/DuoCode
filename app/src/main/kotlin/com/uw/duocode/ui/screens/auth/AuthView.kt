package com.uw.duocode.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uw.duocode.ui.components.TutorialCarousel
import com.uw.duocode.ui.components.TutorialViewModel
import com.uw.duocode.ui.navigation.DASHBOARD

@Composable
fun AuthView(
    navController: NavHostController,
    viewModel: AuthViewModel = viewModel(),
    tutorialViewModel: TutorialViewModel = viewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(viewModel.shouldShowTutorial) {
        if (viewModel.shouldShowTutorial) {
            tutorialViewModel.showTutorial(afterSignup = true)
            viewModel.tutorialShown()
        }
    }
    if (tutorialViewModel.showTutorial) {
        TutorialCarousel(
            slides = tutorialViewModel.tutorialSlides,
            onDismiss = {
                tutorialViewModel.dismissTutorial()
            }
        )
        return
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (viewModel.isLogin) "Login" else "Sign Up",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (!viewModel.isLogin) {
                OutlinedTextField(
                    value = viewModel.name,
                    onValueChange = { viewModel.name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedTextField(
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.password,
                onValueChange = { viewModel.password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.authenticate(
                        context = context,
                        onSuccess = { navController.navigate(DASHBOARD) },
                        onMessage = { message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !viewModel.isLoading,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = if (viewModel.isLogin) "Login" else "Sign Up")
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { viewModel.toggleAuthMode() },
                enabled = !viewModel.isLoading
            ) {
                Text(
                    text = if (viewModel.isLogin)
                        "Need an account? Sign Up"
                    else
                        "Have an account? Login"
                )
            }

            if (viewModel.isLogin) {
                TextButton(
                    onClick = {
                        viewModel.sendResetPassword { message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = !viewModel.isLoading
                ) {
                    Text("Forgot Password?")
                }
            }
        }
        
        if (viewModel.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(50.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
