package com.uw.duocode.ui.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.random.Random

/**
 * Utility class to generate profile pictures and upload them to Firebase Storage
 */
class ProfilePictureGenerator {
    companion object {
        private const val PROFILE_PICTURES_PATH = "profile_pictures"
        private val storage = FirebaseStorage.getInstance()
        private val storageRef = storage.reference
        
        // Colors for profile picture backgrounds
        private val backgroundColors = listOf(
            Color.parseColor("#FF5252"), // Red
            Color.parseColor("#FF4081"), // Pink
            Color.parseColor("#E040FB"), // Purple
            Color.parseColor("#7C4DFF"), // Deep Purple
            Color.parseColor("#536DFE"), // Indigo
            Color.parseColor("#448AFF"), // Blue
            Color.parseColor("#40C4FF"), // Light Blue
            Color.parseColor("#18FFFF"), // Cyan
            Color.parseColor("#64FFDA"), // Teal
            Color.parseColor("#69F0AE"), // Green
            Color.parseColor("#B2FF59"), // Light Green
            Color.parseColor("#EEFF41"), // Lime
            Color.parseColor("#FFFF00"), // Yellow
            Color.parseColor("#FFD740"), // Amber
            Color.parseColor("#FFAB40"), // Orange
            Color.parseColor("#FF6E40")  // Deep Orange
        )
        
        /**
         * Generates a profile picture for a user and uploads it to Firebase Storage
         */
        suspend fun generateAndUploadProfilePicture(
            context: Context,
            userId: String,
            name: String
        ): String = withContext(Dispatchers.IO) {
            val bitmap = generateProfilePicture(context, name)
            
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            
            val profilePictureRef = storageRef.child("$PROFILE_PICTURES_PATH/$userId.jpg")
            val uploadTask = profilePictureRef.putBytes(data).await()
            
            return@withContext profilePictureRef.downloadUrl.await().toString()
        }
        
        /**
         * Uploads a user-selected image from URI to Firebase Storage as profile picture
         */
        suspend fun uploadProfilePictureFromUri(
            context: Context,
            userId: String,
            imageUri: Uri
        ): String = withContext(Dispatchers.IO) {
            try {
                // Get reference to the file location in Firebase Storage
                val profilePictureRef = storageRef.child("$PROFILE_PICTURES_PATH/$userId.jpg")
                
                // Upload the file
                val uploadTask = profilePictureRef.putFile(imageUri).await()
                
                // Return the download URL
                return@withContext profilePictureRef.downloadUrl.await().toString()
            } catch (e: Exception) {
                throw e
            }
        }
        
        /**
         * Generates a profile picture bitmap with the user's initials
         */
        private fun generateProfilePicture(context: Context, name: String): Bitmap {
            val size = 200
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            
            // Draw background
            val backgroundPaint = Paint().apply {
                color = backgroundColors[Random.nextInt(backgroundColors.size)]
                isAntiAlias = true
            }
            canvas.drawCircle(size / 2f, size / 2f, size / 2f, backgroundPaint)
            
            val initials = getInitials(name)
            
            val textPaint = Paint().apply {
                color = Color.WHITE
                textSize = size / 2.5f
                typeface = Typeface.DEFAULT_BOLD
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }
            
            val textBounds = Rect()
            textPaint.getTextBounds(initials, 0, initials.length, textBounds)
            val textHeight = textBounds.height()
            
            canvas.drawText(
                initials,
                size / 2f,
                size / 2f + textHeight / 2f,
                textPaint
            )
            
            return bitmap
        }
        
        /**
         * Extracts initials from a name or email
         */
        private fun getInitials(name: String): String {
            if (name.contains("@")) {
                val username = name.split("@")[0]
                return username.take(1).uppercase()
            }
            val parts = name.split(" ")
            return when {
                parts.isEmpty() -> "?"
                parts.size == 1 -> parts[0].take(1).uppercase()
                else -> "${parts[0].take(1)}${parts[1].take(1)}".uppercase()
            }
        }
    }
} 