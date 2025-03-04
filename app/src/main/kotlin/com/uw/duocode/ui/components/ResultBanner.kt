package com.uw.duocode.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ResultBanner(
    isCorrect: Boolean,
    modifier: Modifier = Modifier,
    message: String? = null
) {
    val backgroundColor = if (isCorrect) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondaryContainer
    val textColor = if (isCorrect) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onSecondaryContainer

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
            .background(color = backgroundColor, shape = RoundedCornerShape(0.dp)),
        contentAlignment = Alignment.TopStart
    ) {
        Text(
            text = message ?: if (isCorrect) "Correct!" else "Incorrect!",
            color = textColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        )
    }
}
