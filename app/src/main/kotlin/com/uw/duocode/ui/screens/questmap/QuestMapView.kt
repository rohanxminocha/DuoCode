package com.uw.duocode.ui.screens.questmap

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.uw.duocode.ui.navigation.LessonArrays
import com.uw.duocode.ui.navigation.Match

@Composable
fun QuestMapView(
    navController: NavHostController
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Welcome back, ${currentUser?.displayName}! \uD83D\uDC4B",
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(20.dp))

        SectionCard(
            title = "Arrays",
            buttonText = "Lessons",
            icon = Icons.Default.Code,
            onButtonClick = {
                navController.navigate(LessonArrays)
            }
        )

        LessonItem(
            title = "Static Arrays",
            icon = Icons.Default.Code,
            showActionButton = true,
            buttonText = "Start",
            onButtonClick = {
                navController.navigate(Match)
            }
        )
        LessonItem(
            title = "Dynamic Arrays",
            icon = Icons.Default.Code,
        )
        LessonItem(
            title = "Stacks",
            icon = Icons.Default.Code
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionCard(
            title = "Linked Lists",
            icon = Icons.Default.SwapHoriz
        )

        LessonItem(
            title = "Singly Linked Lists",
            icon = Icons.Default.SwapHoriz
        )
        LessonItem(
            title = "Doubly Linked Lists",
            icon = Icons.Default.SwapHoriz
        )
        LessonItem(
            title = "Queues",
            icon = Icons.Default.SwapHoriz
        )
    }
}

@Composable
fun SectionCard(
    title: String,
    buttonText: String? = null,
    icon: ImageVector? = null,
    onButtonClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF1EAFE)
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.Gray),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                IconContainer(icon = icon)
            }
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            if (buttonText != null) {
                Button(
                    onClick = onButtonClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7F5CE5),
                        contentColor = Color.White
                    )
                ) {
                    Text(text = buttonText)
                }
            }

        }
    }
}

@Composable
fun LessonItem(
    title: String,
    icon: ImageVector,
    showActionButton: Boolean = false,
    buttonText: String = "Continue",
    onButtonClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconContainer(icon = icon)

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
            modifier = Modifier.weight(1f)
        )

        if (showActionButton) {
            Button(
                onClick = onButtonClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7F5CE5),
                    contentColor = Color.White
                )
            ) {
                Text(text = buttonText)
            }
        }
    }
}

@Composable
fun IconContainer(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color(0xFFD7C3F2)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF7F5CE5)
        )
    }
}
