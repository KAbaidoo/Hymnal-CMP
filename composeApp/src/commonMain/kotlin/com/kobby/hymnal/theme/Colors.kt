package com.kobby.hymnal.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Light Theme Colors
val PurplePrimary = Color(0xFF271E3E)
val YellowAccent = Color(0xFFF7EA2F)
val LightBackground = Color(0xFFF8F7F3)
val LightSurface = Color(0xFFFFFFFF)
val LightTextColor = Color(0xFF0E121B)

// Dark Theme Colors
val DarkBackground = Color(0xFF1A1429)
val DarkSurface = Color(0xFF1A1429)
val DarkTextColor = Color(0xFFFFFFF)

internal val LightColorScheme = lightColorScheme(
    primary = PurplePrimary,
    background = LightBackground,
    onBackground = LightTextColor,
    surface = LightSurface,
    onSurface = LightTextColor,
    secondary = YellowAccent,
    onSecondary = PurplePrimary
)

internal val DarkColorScheme = lightColorScheme(
    primary = PurplePrimary,
    background = DarkBackground,
    onBackground = DarkTextColor,
    surface = DarkSurface,
    onSurface = DarkTextColor,
    secondary = YellowAccent,
    onSecondary = PurplePrimary
)