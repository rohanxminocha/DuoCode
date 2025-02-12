package com.uw.duocode

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun MultipleChoiceScreen() {
    val question_text = "In a graph traversal, which traversal technique explores all neighbours " +
            "of a node before moving on to the next node?"
    val answers = listOf(
        "Depth-First Search (DFS)",
        "Breadth-First Search (BFS)",
        "Dijkstra’s Algorithm",
        "Topological Sort"
    )
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var correctAnswer = "Breadth-First Search (BFS)"
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.height(60.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = question_text,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(50.dp))

        answers.forEach { answer ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .border(
                        width = 2.dp,
                        color = if (selectedAnswer == answer) Color(0xFF6A4CAF) else Color.Gray,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(
                        color = if (selectedAnswer == answer) Color(0xFFEDE7F6) else Color.White,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { selectedAnswer = answer }
                    .padding(26.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = answer, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }
        Spacer(modifier = Modifier.height(55.dp))

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = selectedAnswer != null
        ) {
            Text(text = "Continue", fontSize = 16.sp, color = Color.White)
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewBasicSquareScreen() {
    MultipleChoiceScreen()
}

