package com.uw.duocode.ui.screens.auth

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.uw.duocode.data.model.TopicInfo
import com.uw.duocode.data.model.User
import com.uw.duocode.data.model.UserSubtopicProgress
import com.uw.duocode.ui.screens.challenges.ChallengeData
import com.uw.duocode.ui.screens.profile.AchievementData
import com.uw.duocode.ui.utils.ProfilePictureGenerator
import kotlinx.coroutines.launch


class AuthViewModel : ViewModel() {

    var isLogin by mutableStateOf(true)
        private set

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var name by mutableStateOf("")
    var isLoading by mutableStateOf(false)
        private set
    
    var shouldShowTutorial by mutableStateOf(false)
        private set

    private fun isPasswordValid(password: String): Boolean {
        val lengthRequirement = password.length >= 6
        val uppercaseRequirement = password.any { it.isUpperCase() }
        val lowercaseRequirement = password.any { it.isLowerCase() }
        val digitRequirement = password.any { it.isDigit() }
        val specialCharRequirement = password.any { !it.isLetterOrDigit() }

        return lengthRequirement
                && uppercaseRequirement
                && lowercaseRequirement
                && digitRequirement
                && specialCharRequirement
    }

    fun toggleAuthMode() {
        isLogin = !isLogin
    }

    fun authenticate(
        context: Context,
        onSuccess: () -> Unit,
        onMessage: (String) -> Unit
    ) {
        isLoading = true
        if (email.contains("\\s".toRegex())) {
            isLoading = false
            onMessage("Email cannot contain whitespace")
            return
        }
        if (!isLogin && name.contains("\\s".toRegex())) {
            isLoading = false
            onMessage("Name cannot contain whitespace")
            return
        }

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
            if (password != confirmPassword) {
                isLoading = false
                onMessage("Passwords do not match")
                return
            }
            if (!isPasswordValid(password)) {
                isLoading = false
                onMessage(
                    "Password must be at least 6 characters long and include:\n" +
                            "- 1 uppercase letter\n" +
                            "- 1 lowercase letter\n" +
                            "- 1 digit\n" +
                            "- 1 special character"
                )
                return
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    val user = authResult.user
                    if (user != null) {
                        val profileUpdates =
                            com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build()

                        user.updateProfile(profileUpdates)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    viewModelScope.launch {
                                        try {
                                            val profilePictureUrl =
                                                ProfilePictureGenerator.generateAndUploadProfilePicture(
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
                                            shouldShowTutorial = true
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
                                            shouldShowTutorial = true
                                            onMessage("Account created but couldn't generate profile picture")
                                            onSuccess()
                                        }
                                    }
                                } else {
                                    isLoading = false
                                    shouldShowTutorial = true
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

    fun tutorialShown() {
        shouldShowTutorial = false
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
        userCollection.document(userId).set(newUser)
            .addOnSuccessListener {
                prepopulateUserSubtopicProgress(userId) // prepopulate progress records for all subtopics
                prepopulateUserChallenges(userId)
                prepopulateUserAchievements(userId)
            }
            .addOnFailureListener { e ->
                println("Error creating user document for UID: $userId, error: ${e.message}")
            }
    }

    private fun prepopulateUserSubtopicProgress(userId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("subtopics")
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.documents.forEach { doc ->
                    val subtopicId = doc.id
                    val progress = UserSubtopicProgress(
                        id = subtopicId,
                        completed = false,
                        correctAnswers = 0
                    )
                    db.collection("users").document(userId)
                        .collection("subtopics")
                        .document(subtopicId)
                        .set(progress)
                        .addOnFailureListener { e ->
                            println("Failed to prepopulate progress for subtopic: $subtopicId, error: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                println("Error fetching subtopics for prepopulation: ${e.message}")
            }
    }

    private fun prepopulateUserChallenges(userId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("subtopics")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val batch = db.batch()
                querySnapshot.documents.forEach { doc ->
                    val subtopicId = doc.id
                    val subtopicName = doc.getString("name") ?: subtopicId
                    listOf(
                        2 to "Beginner",
                        5 to "Intermediate",
                        10 to "Expert"
                    ).forEach { (threshold, levelName) ->
                        val challengeId = "$subtopicId-$levelName"

                        val challenge = ChallengeData(
                            title = "$subtopicName $levelName",
                            subTitle = "Finish $threshold questions in $subtopicName",
                            completed = false,
                            dateCompleted = null,
                            subtopicId = subtopicId
                        )

                        val challengeDocRef = db.collection("users")
                            .document(userId)
                            .collection("challenges")
                            .document(challengeId)

                        batch.set(challengeDocRef, challenge)
                    }
                }
                batch.commit()
                    .addOnSuccessListener {
                        println("Successfully prepopulated challenges for user: $userId")
                    }
                    .addOnFailureListener { e ->
                        println("Failed to commit challenge batch: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                println("Error fetching subtopics for challenge prepopulation: ${e.message}")
            }
    }

    private fun prepopulateUserAchievements(userId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("topics")
            .get()
            .addOnSuccessListener { topicsSnapshot ->
                val batch = db.batch()
                topicsSnapshot.documents.forEach { doc ->
                    val topic = doc.toObject<TopicInfo>()
                    if (topic != null) {
                        val achievement = AchievementData(
                            id = doc.id,
                            title = topic.name,
                            description = "Mastered ${topic.name}",
                            iconName = topic.iconKey,
                            unlocked = false,
                            dateUnlocked = null
                        )
                        val achievementDocRef = db.collection("users")
                            .document(userId)
                            .collection("achievements")
                            .document(doc.id)
                        batch.set(achievementDocRef, achievement)
                    }
                }
                batch.commit()
                    .addOnSuccessListener {
                        println("Achievements prepopulated for user: $userId")
                    }
                    .addOnFailureListener { e ->
                        println("Error prepopulating achievements: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                println("Error fetching topics for achievements: ${e.message}")
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
