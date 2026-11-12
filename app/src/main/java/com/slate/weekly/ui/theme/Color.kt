package com.slate.weekly.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val Paper = Color(0xFFF3F3F0)
val Ink = Color(0xFF0B0B0C)
val Graphite = Color(0xFF66666A)
val Hairline = Color(0xFFE3E3DE)
val OutlineLight = Color(0xFFD0D0CA)

val Night = Color(0xFF09090A)
val Chalk = Color(0xFFF4F4F2)
val Ash = Color(0xFFA6A6AA)
val NightHairline = Color(0xFF2B2B30)
val OutlineDark = Color(0xFF3A3A41)

internal val LightScheme = lightColorScheme(
    primary = Ink,
    onPrimary = Paper,
    primaryContainer = Color(0xFFEAEAE5),
    onPrimaryContainer = Ink,
    inversePrimary = Chalk,
    secondary = Graphite,
    onSecondary = Paper,
    secondaryContainer = Color(0xFFEAEAE5),
    onSecondaryContainer = Ink,
    tertiary = Graphite,
    onTertiary = Paper,
    tertiaryContainer = Color(0xFFEAEAE5),
    onTertiaryContainer = Ink,
    background = Paper,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = Color(0xFFECECE7),
    onSurfaceVariant = Graphite,
    surfaceTint = Color.Transparent,
    inverseSurface = Ink,
    inverseOnSurface = Paper,
    error = Ink,
    onError = Paper,
    errorContainer = Color(0xFFEFEFEA),
    onErrorContainer = Ink,
    outline = OutlineLight,
    outlineVariant = Hairline,
    scrim = Color.Black,
    surfaceBright = Color(0xFFFBFBF9),
    surfaceDim = Color(0xFFE7E7E2),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFFFFFF),
    surfaceContainer = Color(0xFFEFEFEA),
    surfaceContainerHigh = Color(0xFFE9E9E4),
    surfaceContainerHighest = Color(0xFFE2E2DC),
)

internal val DarkScheme = darkColorScheme(
    primary = Chalk,
    onPrimary = Night,
    primaryContainer = Color(0xFF202024),
    onPrimaryContainer = Chalk,
    inversePrimary = Ink,
    secondary = Ash,
    onSecondary = Night,
    secondaryContainer = Color(0xFF202024),
    onSecondaryContainer = Chalk,
    tertiary = Ash,
    onTertiary = Night,
    tertiaryContainer = Color(0xFF202024),
    onTertiaryContainer = Chalk,
    background = Night,
    onBackground = Chalk,
    surface = Night,
    onSurface = Chalk,
    surfaceVariant = Color(0xFF1C1C20),
    onSurfaceVariant = Ash,
    surfaceTint = Color.Transparent,
    inverseSurface = Chalk,
    inverseOnSurface = Night,
    error = Chalk,
    onError = Night,
    errorContainer = Color(0xFF202024),
    onErrorContainer = Chalk,
    outline = OutlineDark,
    outlineVariant = NightHairline,
    scrim = Color.Black,
    surfaceBright = Color(0xFF26262B),
    surfaceDim = Night,
    surfaceContainerLowest = Color(0xFF0D0D0F),
    surfaceContainerLow = Color(0xFF17171A),
    surfaceContainer = Color(0xFF1D1D21),
    surfaceContainerHigh = Color(0xFF232328),
    surfaceContainerHighest = Color(0xFF2A2A2F),
)
