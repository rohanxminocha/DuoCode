package com.uw.duocode.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.uw.duocode.R
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
    val image = painterResource(R.drawable.duocode)

    if (viewModel.shouldShowTutorial) {
        tutorialViewModel.showTutorial(afterSignup = true)
        viewModel.tutorialShown()
    }

    if (tutorialViewModel.showTutorial) {
        TutorialCarousel(
            slides = tutorialViewModel.tutorialSlides,
            onDismiss = {
                tutorialViewModel.dismissTutorial()
                navController.navigate(DASHBOARD)
            }
        )
        return
    }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    fun isPasswordValid(password: String): Boolean {
        val lengthRequirement = password.length >= 6
        val uppercaseRequirement = password.any { it.isUpperCase() }
        val lowercaseRequirement = password.any { it.isLowerCase() }
        val digitRequirement = password.any { it.isDigit() }
        val specialCharRequirement = password.any { !it.isLetterOrDigit() }
        return lengthRequirement && uppercaseRequirement && lowercaseRequirement &&
                digitRequirement && specialCharRequirement
    }

    fun missingRequirements(password: String): List<String> {
        val missing = mutableListOf<String>()
        if (password.length < 6) {
            missing.add("at least 6 characters")
        }
        if (!password.any { it.isUpperCase() }) {
            missing.add("an uppercase letter")
        }
        if (!password.any { it.isLowerCase() }) {
            missing.add("a lowercase letter")
        }
        if (!password.any { it.isDigit() }) {
            missing.add("a digit")
        }
        if (!password.any { !it.isLetterOrDigit() }) {
            missing.add("a special character")
        }
        return missing
    }

    val isSignUpEnabled = viewModel.isLogin ||
            (isPasswordValid(viewModel.password)
                    && viewModel.password == viewModel.confirmPassword)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.surface
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = image,
                contentDescription = "DuoCode Logo",
                modifier = Modifier.size(180.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "DuoCode",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (viewModel.isLogin)
                    "Resume your coding journey"
                else
                    "Join us to begin your coding journey",
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (!viewModel.isLogin) {
                OutlinedTextField(
                    value = viewModel.name,
                    onValueChange = {
                        viewModel.name = it.replace("\\s".toRegex(), "")
                    },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedTextField(
                value = viewModel.email,
                onValueChange = {
                    viewModel.email = it.replace("\\s".toRegex(), "")
                },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            val showPasswordError = !viewModel.isLogin &&
                    viewModel.password.isNotEmpty() &&
                    !isPasswordValid(viewModel.password)

            OutlinedTextField(
                value = viewModel.password,
                onValueChange = { viewModel.password = it },
                label = { Text("Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = Color.Gray
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                ),
                isError = showPasswordError
            )

            if (showPasswordError) {
                val missing = missingRequirements(viewModel.password)
                val errorMessage = "Password must include: " + missing.joinToString(", ")
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, start = 16.dp)
                )
            }

            if (!viewModel.isLogin) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = viewModel.confirmPassword,
                    onValueChange = { viewModel.confirmPassword = it },
                    label = { Text("Confirm Password") },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(
                            onClick = { confirmPasswordVisible = !confirmPasswordVisible },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                tint = Color.Gray
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    ),
                    isError = viewModel.confirmPassword.isNotEmpty() &&
                            viewModel.confirmPassword != viewModel.password
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.authenticate(
                        context = context,
                        onSuccess = {
                            if (viewModel.isLogin) {
                                navController.navigate(DASHBOARD)
                            }
                        },
                        onMessage = { message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !viewModel.isLoading && isSignUpEnabled,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (viewModel.isLogin) "Login" else "Sign Up",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { viewModel.toggleAuthMode() },
                enabled = !viewModel.isLoading,
                modifier = Modifier
                    .height(36.dp)
                    .padding(top = 2.dp)
            ) {
                Text(
                    text = if (viewModel.isLogin)
                        "Don't have an account? Sign Up"
                    else
                        "Already have an account? Sign In"
                )
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
