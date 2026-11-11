package com.slate.weekly.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.slate.weekly.R

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

private val LightScheme = lightColorScheme(
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

private val DarkScheme = darkColorScheme(
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

val SlateFont = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
)

private val SlateTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = SlateFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 40.sp,
        lineHeight = 44.sp,
        letterSpacing = (-1.4).sp,
        fontFeatureSettings = "tnum",
    ),
    headlineLarge = TextStyle(
        fontFamily = SlateFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp,
        lineHeight = 35.sp,
        letterSpacing = (-0.9).sp,
        fontFeatureSettings = "tnum",
    ),
    titleMedium = TextStyle(
        fontFamily = SlateFont,
        fontWeight = FontWeight.Medium,
        fontSize = 17.sp,
        lineHeight = 23.sp,
        letterSpacing = (-0.2).sp,
        fontFeatureSettings = "tnum",
    ),
    titleSmall = TextStyle(
        fontFamily = SlateFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 19.sp,
        fontFeatureSettings = "tnum",
    ),
    bodyLarge = TextStyle(
        fontFamily = SlateFont,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 23.sp,
        fontFeatureSettings = "tnum",
    ),
    bodyMedium = TextStyle(
        fontFamily = SlateFont,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 19.sp,
        fontFeatureSettings = "tnum",
    ),
    bodySmall = TextStyle(
        fontFamily = SlateFont,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        fontFeatureSettings = "tnum",
    ),
    labelMedium = TextStyle(
        fontFamily = SlateFont,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.1.sp,
        fontFeatureSettings = "tnum",
    ),
    labelSmall = TextStyle(
        fontFamily = SlateFont,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 13.sp,
        letterSpacing = 0.3.sp,
        fontFeatureSettings = "tnum",
    ),
    labelLarge = TextStyle(
        fontFamily = SlateFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        fontFeatureSettings = "tnum",
    ),
)

@Composable
fun SlateTheme(dark: Boolean, content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !dark
            controller.isAppearanceLightNavigationBars = !dark
        }
    }
    MaterialTheme(
        colorScheme = if (dark) DarkScheme else LightScheme,
        typography = SlateTypography,
        content = content,
    )
}
