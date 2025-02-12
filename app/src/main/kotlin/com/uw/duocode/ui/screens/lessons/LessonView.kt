package com.uw.duocode.ui.screens.lessons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
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
import com.uw.duocode.ui.navigation.Match

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonView(
    navController: NavHostController,
    topicName: String = "Arrays",
    topicDescription: String = """
        An array is a fundamental data structure 
        that holds a collection of elements in a contiguous block of memory.
        Arrays allow efficient retrieval and modification of elements by position.

        Here’s how to instantiate one in Python:
        
        arr = ["apple", "banana", "orange"]
    """.trimIndent(),
    subtopics: List<String> = listOf("Dynamic Arrays", "Top K Elements", "Prefix Sums")
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SmallTopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )

        TopicHeader(icon = Icons.Default.Code, topicName = topicName)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Code,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = topicDescription,
            style = TextStyle(fontSize = 16.sp),
            modifier = Modifier.padding(16.dp)
        )

        Text(
            text = "Subtopics:",
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        subtopics.forEach { subtopic ->
            Text(
                text = "• $subtopic",
                style = TextStyle(fontSize = 14.sp),
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Button(
                onClick = {
                    navController.navigate(Match)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7F5CE5),
                    contentColor = Color.White
                )
            ) {
                Text("Begin Quest!")
            }
        }
    }
}

@Composable
fun TopicHeader(icon: ImageVector, topicName: String) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = topicName,
            style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold)
        )
    }
}
