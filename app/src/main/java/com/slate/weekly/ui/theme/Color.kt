package com.slate.weekly.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val Paper = Color(0xFFF7F7F6)
val Ink = Color(0xFF0D0D0E)
val Graphite = Color(0xFF6C6C6F)
val Hairline = Color(0xFFE7E7E3)
val OutlineLight = Color(0xFFD8D8D3)

val Night = Color(0xFF0A0A0B)
val Chalk = Color(0xFFF2F2F0)
val Ash = Color(0xFF949496)
val NightHairline = Color(0xFF1D1D1F)
val OutlineDark = Color(0xFF2E2E31)

internal val LightScheme = lightColorScheme(
    primary = Ink,
    onPrimary = Paper,
    primaryContainer = Color(0xFFEDEDEA),
    onPrimaryContainer = Ink,
    inversePrimary = Chalk,
    secondary = Graphite,
    onSecondary = Paper,
    secondaryContainer = Color(0xFFEDEDEA),
    onSecondaryContainer = Ink,
    tertiary = Graphite,
    onTertiary = Paper,
    tertiaryContainer = Color(0xFFEDEDEA),
    onTertiaryContainer = Ink,
    background = Paper,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = Color(0xFFEFEFEC),
    onSurfaceVariant = Graphite,
    surfaceTint = Color.Transparent,
    inverseSurface = Ink,
    inverseOnSurface = Paper,
    error = Ink,
    onError = Paper,
    errorContainer = Color(0xFFEFEFEC),
    onErrorContainer = Ink,
    outline = OutlineLight,
    outlineVariant = Hairline,
    scrim = Color.Black,
    surfaceBright = Paper,
    surfaceDim = Color(0xFFECECE8),
    surfaceContainerLowest = Paper,
    surfaceContainerLow = Color(0xFFF4F4F1),
    surfaceContainer = Color(0xFFF1F1EE),
    surfaceContainerHigh = Color(0xFFEEEEEA),
    surfaceContainerHighest = Color(0xFFEAEAE6),
)

internal val DarkScheme = darkColorScheme(
    primary = Chalk,
    onPrimary = Night,
    primaryContainer = Color(0xFF1C1C1E),
    onPrimaryContainer = Chalk,
    inversePrimary = Ink,
    secondary = Ash,
    onSecondary = Night,
    secondaryContainer = Color(0xFF1C1C1E),
    onSecondaryContainer = Chalk,
    tertiary = Ash,
    onTertiary = Night,
    tertiaryContainer = Color(0xFF1C1C1E),
    onTertiaryContainer = Chalk,
    background = Night,
    onBackground = Chalk,
    surface = Night,
    onSurface = Chalk,
    surfaceVariant = Color(0xFF141416),
    onSurfaceVariant = Ash,
    surfaceTint = Color.Transparent,
    inverseSurface = Chalk,
    inverseOnSurface = Night,
    error = Chalk,
    onError = Night,
    errorContainer = Color(0xFF1C1C1E),
    onErrorContainer = Chalk,
    outline = OutlineDark,
    outlineVariant = NightHairline,
    scrim = Color.Black,
    surfaceBright = Color(0xFF1A1A1C),
    surfaceDim = Night,
    surfaceContainerLowest = Night,
    surfaceContainerLow = Color(0xFF0F0F11),
    surfaceContainer = Color(0xFF141416),
    surfaceContainerHigh = Color(0xFF18181A),
    surfaceContainerHighest = Color(0xFF1D1D1F),
)
