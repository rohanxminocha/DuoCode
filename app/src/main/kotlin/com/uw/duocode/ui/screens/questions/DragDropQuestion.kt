package com.uw.duocode

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sh.calvin.reorderable.ReorderableColumn


@Composable
fun DragAndDropMatchingQuestion() {
    val haptic = LocalHapticFeedback.current

    var steps by remember {
        mutableStateOf(
            listOf(
                "Sort each half recursively",
                "Split the list into two halves",
                "Merge the two sorted halves"
            )
        )
    }

    // Correct order for validation
    val correctOrder = listOf(
        "Split the list into two halves",
        "Sort each half recursively",
        "Merge the two sorted halves"
    )

    var isCorrect by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Drag these steps to order them correctly for the Merge Sort algorithm.",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(25.dp)
        )

        ReorderableColumn(
            modifier = Modifier.fillMaxWidth().padding(25.dp),
            list = steps,
            onSettle = { fromIndex, toIndex ->
                steps = steps.toMutableList().apply {
                    add(toIndex, removeAt(fromIndex))
                }
            },
            onMove = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            },
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) { index, step, _ ->
            key(step) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .draggableHandle(
                            onDragStarted = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            onDragStopped = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        ),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(3.dp, Color.LightGray)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(text = step, fontSize = 20.sp, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
                    }
                }
            }
        }

        Column(
            Modifier.background(if(isCorrect) Color.LightGray else MaterialTheme.colorScheme.surface)
                .fillMaxWidth()
                .height(200.dp)
                .padding(25.dp)
        ){
            Text(
                text = if(isCorrect) "Correct!" else "",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
            Button(
                onClick = { isCorrect = steps == correctOrder },
                modifier = Modifier.fillMaxWidth().padding(top = 50.dp)
            ) {
                Text(text = if(isCorrect) "Continue" else "Check")
            }
        }
    }
}