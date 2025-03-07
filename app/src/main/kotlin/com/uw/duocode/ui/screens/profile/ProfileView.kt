package com.uw.duocode.ui.screens.profile

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.uw.duocode.MainActivity
import com.uw.duocode.ui.navigation.AUTH
import com.uw.duocode.ui.notification.NotificationReceiver
import java.util.Calendar

@Composable
fun ProfileView(
    navController: NavHostController,
    profileViewModel: ProfileViewModel = viewModel(),
    friendViewModel: FriendViewModel = viewModel()
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("notification_settings", Context.MODE_PRIVATE)
    val enabledNotifications = remember { mutableStateOf(
        sharedPreferences.getBoolean("enabled_notifications", false)
    ) }
    val notificationEnabled = remember { mutableStateOf(enabledNotifications.value) }
    
    var showFriendsView by remember { mutableStateOf(false) }
    
    LaunchedEffect(key1 = Unit) {
        profileViewModel.loadUserData()
        friendViewModel.loadPendingRequests()
    }
    
    var showProfilePictureOptions by remember { mutableStateOf(false) }
    
    var showPermissionDialog by remember { mutableStateOf(false) }
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            profileViewModel.updateProfilePicture(
                context = context,
                imageUri = it,
                onSuccess = {
                    profileViewModel.loadUserData()
                }
            )
        }
    }
    
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            imagePickerLauncher.launch("image/*")
        } else {
            showPermissionDialog = true
        }
    }
    
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Permission Required") },
            text = { Text("Storage permission is required to select a profile picture. Please grant this permission in your device settings.") },
            confirmButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
    
    if (profileViewModel.errorMessage != null) {
        AlertDialog(
            onDismissRequest = { profileViewModel.clearError() },
            title = { Text("Error") },
            text = { Text(profileViewModel.errorMessage ?: "") },
            confirmButton = {
                TextButton(onClick = { profileViewModel.clearError() }) {
                    Text("OK")
                }
            }
        )
    }
    
    if (showProfilePictureOptions) {
        Dialog(onDismissRequest = { showProfilePictureOptions = false }) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Change Profile Picture",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Button(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.READ_MEDIA_IMAGES
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                            } else {
                                imagePickerLauncher.launch("image/*")
                            }
                        } else {
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            } else {
                                imagePickerLauncher.launch("image/*")
                            }
                        }
                        showProfilePictureOptions = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Upload from gallery",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Upload from Gallery")
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = {
                        profileViewModel.generateNewProfilePicture(
                            context = context,
                            onSuccess = {
                                profileViewModel.loadUserData()
                            }
                        )
                        showProfilePictureOptions = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Generate new",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Generate New")
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                TextButton(
                    onClick = { showProfilePictureOptions = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel")
                }
            }
        }
    }
    
    if (showFriendsView) {
        Dialog(
            onDismissRequest = { showFriendsView = false }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                FriendsView(friendViewModel)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .clickable { showProfilePictureOptions = true },
            contentAlignment = Alignment.Center
        ) {
            if (profileViewModel.isLoading || profileViewModel.isUpdatingProfilePicture) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(profileViewModel.user?.profilePictureUrl)
                        .crossfade(true)
                        .diskCachePolicy(coil.request.CachePolicy.DISABLED)
                        .memoryCachePolicy(coil.request.CachePolicy.DISABLED)
                        .build(),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .fillMaxSize()
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentScale = ContentScale.Crop,
                    loading = {
                        CircularProgressIndicator(
                            modifier = Modifier.size(40.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    error = {
                        val auth = FirebaseAuth.getInstance()
                        val currentUser = auth.currentUser
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = getInitials(currentUser?.displayName ?: currentUser?.email ?: ""),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                    }
                )
                
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                ) {
                    FloatingActionButton(
                        onClick = { showProfilePictureOptions = true },
                        modifier = Modifier.size(36.dp),
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Change profile picture",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            user.displayName?.let { name ->
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = user.email ?: "",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BadgedBox(
                        badge = {
                            if (friendViewModel.pendingRequests.isNotEmpty()) {
                                Badge {
                                    Text(text = friendViewModel.pendingRequests.size.toString())
                                }
                            }
                        }
                    ) {
                        Text(
                            text = "Friends",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Button(
                        onClick = { showFriendsView = true }
                    ) {
                        Text("Manage Friends")
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                if (friendViewModel.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (friendViewModel.friends.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "You don't have any friends yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    // Show up to 3 friends
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        friendViewModel.friends.take(3).forEach { friend ->
                            FriendItem(friend)
                        }
                        
                        if (friendViewModel.friends.size > 3) {
                            TextButton(
                                onClick = { showFriendsView = true },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("View all ${friendViewModel.friends.size} friends")
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Enable Reminders",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                LaunchedEffect(enabledNotifications.value) {
                    if (enabledNotifications.value) {
                        scheduleDailyNotification(context)
                    } else {
                        cancelNotification(context)
                    }
                }

                Switch(
                    checked = notificationEnabled.value,
                    onCheckedChange = { isChecked ->
                        notificationEnabled.value = isChecked
                        if (isChecked) {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(
                                    context as Activity,
                                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                                    1
                                )
                            } else {
                                enabledNotifications.value = true
                                sharedPreferences.edit().putBoolean("enabled_notifications", true).apply()
                            }
                            scheduleDailyNotification(context)
                        } else {
                            enabledNotifications.value = false
                            sharedPreferences.edit().putBoolean("enabled_notifications", false).apply()
                            cancelNotification(context)
                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Test Notification",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { showTestNotification(context) }
                    .padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                auth.signOut()
                navController.navigate(AUTH)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6A4CAF),
                contentColor = Color.White
            ),
        ) {
            Text("Sign Out")
        }
    }
}

private fun getInitials(nameOrEmail: String): String {
    return if (nameOrEmail.contains("@")) {
        nameOrEmail.split("@")[0].take(1).uppercase()
    } else {
        val parts = nameOrEmail.split(" ")
        when {
            parts.isEmpty() -> "?"
            parts.size == 1 -> parts[0].take(1).uppercase()
            else -> "${parts[0].take(1)}${parts[1].take(1)}".uppercase()
        }
    }
}

private fun scheduleDailyNotification(context: Context) {
    // Reminder for 6PM
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 18)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }

    val intent = Intent(context, NotificationReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setRepeating(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        AlarmManager.INTERVAL_DAY,
        pendingIntent
    )
}

private fun cancelNotification(context: Context) {
    val intent = Intent(context, NotificationReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(pendingIntent)
}

fun showTestNotification(context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        context, 0, intent, PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, "reminder_channel_id")
        .setSmallIcon(android.R.drawable.ic_popup_reminder)
        .setContentTitle("DuoCode Test")
        .setContentText("This is a test notification.")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setChannelId("reminder_channel_id")
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            1
        )
    }

    NotificationManagerCompat.from(context).notify(1, notification.build())
}