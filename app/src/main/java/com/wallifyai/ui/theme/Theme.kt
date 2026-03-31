package com.wallifyai.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.wallifyai.domain.model.ThemeMode

private val LightColors = lightColorScheme(
    primary = Color(0xFF1967D2),
    onPrimary = Color.White,
    secondary = Color(0xFF0F766E),
    tertiary = Color(0xFF7C3AED),
    background = Color(0xFFF6F8FB),
    surface = Color.White,
    surfaceVariant = Color(0xFFE8EEF7),
    onSurface = Color(0xFF121826),
    onSurfaceVariant = Color(0xFF516076),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8EB8FF),
    onPrimary = Color(0xFF052D61),
    secondary = Color(0xFF7FE7D6),
    tertiary = Color(0xFFD5B8FF),
    background = Color(0xFF09111F),
    surface = Color(0xFF111B2E),
    surfaceVariant = Color(0xFF1A2740),
    onSurface = Color(0xFFF0F4FA),
    onSurfaceVariant = Color(0xFFB9C4D8),
)

@Composable
fun WallifyTheme(
    themeMode: ThemeMode,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
