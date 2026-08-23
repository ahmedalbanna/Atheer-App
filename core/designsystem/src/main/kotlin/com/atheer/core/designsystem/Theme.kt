package com.atheer.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Red60,
    secondary = AccentCyan,
    tertiary = ActiveGreen,
    background = DarkBg,
    surface = DarkCard,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    onBackground = androidx.compose.ui.graphics.Color.White,
    onSurface = androidx.compose.ui.graphics.Color.White,
)

private val LightColorScheme = lightColorScheme(
    primary = Red60,
    secondary = AccentCyan,
    tertiary = ActiveGreen,
    background = DarkBg,
    surface = DarkCard,
)

/**
 * Atheer brand theme. Cinematic dark mode is the product identity, so it is
 * forced regardless of the system setting.
 */
@Composable
fun AtheerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AtheerTypography,
        content = content,
    )
}
