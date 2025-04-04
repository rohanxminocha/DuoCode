package com.uw.duocode.ui.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

data class AchievementData(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val iconName: String = "",
    val unlocked: Boolean = false,
    val dateUnlocked: Date? = null
)

class AchievementsViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {
    var achievements by mutableStateOf<List<AchievementData>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    private val userId: String get() = auth.currentUser?.uid ?: ""

    fun loadAchievements() {
        isLoading = true
        error = null

        db.collection("users")
            .document(userId)
            .collection("achievements")
            .get()
            .addOnSuccessListener { snapshot ->
                achievements = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(AchievementData::class.java)
                }
                isLoading = false
            }
            .addOnFailureListener { e ->
                error = "Error loading achievements: ${e.message}"
                isLoading = false
            }
    }

    fun unlockAchievement(achievementId: String) {
        val userDocRef = db.collection("users").document(userId)
        val achievementRef = userDocRef.collection("achievements").document(achievementId)
        achievementRef.update(
            mapOf(
                "unlocked" to true,
                "dateUnlocked" to Date()
            )
        ).addOnSuccessListener {
            loadAchievements()
        }.addOnFailureListener { e ->
            error = "Error unlocking achievement: ${e.message}"
        }
    }
}
