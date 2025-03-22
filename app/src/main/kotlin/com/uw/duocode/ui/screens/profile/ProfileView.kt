package com.uw.duocode.ui.screens.profile

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.uw.duocode.data.model.User
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileView(
    navController: NavHostController,
    profileViewModel: ProfileViewModel = viewModel(),
    friendViewModel: FriendViewModel = viewModel()
) {
    val context = LocalContext.current
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
                onSuccess = { profileViewModel.loadUserData() }
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
            ChangeProfilePictureDialog(
                onUploadGallery = {
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
                onGenerateNew = {
                    profileViewModel.generateNewProfilePicture(context) {
                        profileViewModel.loadUserData()
                    }
                    showProfilePictureOptions = false
                },
                onCancel = { showProfilePictureOptions = false }
            )
        }
    }

    if (showFriendsView) {
        Dialog(onDismissRequest = { showFriendsView = false }) {
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

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navController.navigate("settings") }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfilePictureSection(
                isLoading = profileViewModel.isLoading,
                isUpdating = profileViewModel.isUpdatingProfilePicture,
                profilePictureUrl = profileViewModel.user?.profilePictureUrl,
                onClick = { showProfilePictureOptions = true }
            )

            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser
            currentUser?.let { user ->
                user.displayName?.let { name ->
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = user.email ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OverviewSection(profileViewModel.user)

            FriendsCard(
                friendViewModel = friendViewModel,
                onManageFriends = { showFriendsView = true }
            )
        }
    }
}

@Composable
private fun ChangeProfilePictureDialog(
    onUploadGallery: () -> Unit,
    onGenerateNew: () -> Unit,
    onCancel: () -> Unit
) {
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
            onClick = onUploadGallery,
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
            onClick = onGenerateNew,
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
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }
    }
}

@Composable
private fun ProfilePictureSection(
    isLoading: Boolean,
    isUpdating: Boolean,
    profilePictureUrl: String?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isLoading || isUpdating) {
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
                model = ImageRequest.Builder(LocalContext.current)
                    .data(profilePictureUrl)
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
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = getInitials(
                                currentUser?.displayName ?: currentUser?.email ?: ""
                            ),
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
                    onClick = onClick,
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
}

@Composable
private fun OverviewSection(user: User?) {
    val dailyStreak = user?.currentStreak ?: 0
    val level = user?.level ?: "Apprentice Coder"
    val totalQuestions = user?.totalQuestionsAttempted ?: 0
    val totalCorrect = user?.totalCorrectAnswers ?: 0
    val accuracy =
        if (totalQuestions > 0) (totalCorrect.toFloat() / totalQuestions * 100).roundToInt() else 0
    val timeSpent = user?.totalTimeSpentInMinutes ?: 0L

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Overview",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OverviewCard(
                title = "Daily Streak",
                value = "$dailyStreak",
                icon = Icons.Filled.Star,
                modifier = Modifier.weight(1f)
            )

            OverviewCard(
                title = "Level",
                value = level,
                icon = Icons.Filled.EmojiEvents,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OverviewCard(
                title = "Accuracy",
                value = "$accuracy%",
                icon = Icons.Filled.CheckCircle,
                modifier = Modifier.weight(1f)
            )

            OverviewCard(
                title = "Time Spent",
                value = "$timeSpent mins",
                icon = Icons.Filled.Timer,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun OverviewCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(70.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondaryContainer
                )
            }
        }
    }
}

@Composable
private fun FriendsCard(
    friendViewModel: FriendViewModel,
    onManageFriends: () -> Unit
) {
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
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Button(onClick = onManageFriends) {
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
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    friendViewModel.friends.take(3).forEach { friend ->
                        FriendItem(friend)
                    }
                    if (friendViewModel.friends.size > 3) {
                        TextButton(
                            onClick = onManageFriends,
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("View all ${friendViewModel.friends.size} friends")
                        }
                    }
                }
            }
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
