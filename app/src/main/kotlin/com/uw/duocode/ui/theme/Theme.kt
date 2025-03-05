package com.uw.duocode.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6A4CAF),
    onPrimary = Color.White,
    secondary = Color(0xFF7F5CE5),
    onSecondary = Color.White,
    tertiary = Color(0xFF4CAF50),
    onTertiary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFE7E0F1),
    onSurfaceVariant = Color.Black,
    secondaryContainer = Color(0xFFCCC2DC),
    onSecondaryContainer = Color.Black,
    outline = Color.LightGray,
    error = Color(0xFFD32F2F)
)

@Composable
fun DuocodeTheme(
    content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}