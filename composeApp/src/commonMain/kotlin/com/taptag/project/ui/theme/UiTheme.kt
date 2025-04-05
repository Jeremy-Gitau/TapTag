package com.taptag.project.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object NFCScannerTheme {
    val DarkBackground = Color(0xFF111827)
    val DarkSurface = Color(0xFF1F2937)
    val Primary = Color(0xFF10B981)
    val PrimaryGreen = Color(0xFF237456)
    val PrimaryGreenDarker = Color(0xFF031B13)
    val PrimaryGreenLighter = Color(0xFF6EE7B7)
    val TextWhite = Color.White
    val TextGray = Color(0xFF9CA3AF)
    val TextGrayDarker = Color(0xFF6B7280)
    val DividerColor = Color(0xFF374151)

    val Black = Color(0xFF121212)
    val White = Color(0xFFF4F4F4)
    val BackGroundWhite = Color(0xFFE8EBE9)
}

private val darkColorPalette = darkColorScheme(
    primary = NFCScannerTheme.Primary,
    onPrimary = NFCScannerTheme.TextWhite,
    primaryContainer = NFCScannerTheme.PrimaryGreen,
    onPrimaryContainer = NFCScannerTheme.TextWhite,

    secondary = NFCScannerTheme.PrimaryGreenLighter,
    onSecondary = NFCScannerTheme.Black,
    secondaryContainer = NFCScannerTheme.PrimaryGreenDarker,
    onSecondaryContainer = NFCScannerTheme.TextWhite,

    background = NFCScannerTheme.DarkBackground,
    onBackground = NFCScannerTheme.TextWhite,

    surface = NFCScannerTheme.DarkSurface,
    onSurface = NFCScannerTheme.White,

    surfaceVariant = NFCScannerTheme.DividerColor,
    onSurfaceVariant = NFCScannerTheme.TextGrayDarker,

    error = Color(0xFFCF6679),
    onError = NFCScannerTheme.Black
)

private val lightColorPalette = lightColorScheme(
    primary = NFCScannerTheme.Primary,
    onPrimary = NFCScannerTheme.White,
    primaryContainer = NFCScannerTheme.PrimaryGreenLighter,
    onPrimaryContainer = NFCScannerTheme.Black,

    secondary = NFCScannerTheme.Primary,
    onSecondary = NFCScannerTheme.White,
    secondaryContainer = NFCScannerTheme.PrimaryGreenDarker,
    onSecondaryContainer = NFCScannerTheme.TextWhite,

    background = NFCScannerTheme.BackGroundWhite,
    onBackground = NFCScannerTheme.Black,

    surface = NFCScannerTheme.White,  // Slight off-white surface
    onSurface = NFCScannerTheme.Black,

    surfaceVariant = Color(0xFFFAF9F9),  // Light gray surface variant
    onSurfaceVariant = NFCScannerTheme.TextGrayDarker,

    error = Color(0xFFF15470),
    onError = NFCScannerTheme.White
)

@Composable
fun NFCScannerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable (() -> Unit)
) {
    val colorScheme = if (darkTheme) darkColorPalette else lightColorPalette

    println("app theme : $darkTheme")

    println("color scheme : $colorScheme")

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes
    )
}
