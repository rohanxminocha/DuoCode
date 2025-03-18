package com.uw.duocode.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
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
    secondaryContainer = Color(0xFF9982C5),
    onSecondaryContainer = Color.Black,
    outline = Color.LightGray,
    error = Color(0xFFD32F2F),
    primaryContainer = Color(0x336A4CAF)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFB39DDB),
    onPrimary = Color.Black,
    secondary = Color(0xFF9575CD),
    onSecondary = Color.Black,
    tertiary = Color(0xFF66BB6A),
    onTertiary = Color.Black,
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color.White,
    secondaryContainer = Color(0xFF7E57C2),
    onSecondaryContainer = Color.White,
    outline = Color(0xFF8A8A8A),
    error = Color(0xFFCF6679),
    primaryContainer = Color(0xFF4527A0)
)

@Composable
fun DuocodeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
