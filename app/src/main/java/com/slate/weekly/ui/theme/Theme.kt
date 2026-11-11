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
val Graphite = Color(0xFF77777A)
val Hairline = Color(0xFFE3E3DF)

val Night = Color(0xFF0A0A0B)
val Chalk = Color(0xFFF2F2F0)
val Ash = Color(0xFF8B8B8D)
val NightHairline = Color(0xFF262628)

private val LightScheme = lightColorScheme(
    primary = Ink,
    onPrimary = Paper,
    background = Paper,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = Color(0xFFEFEFEC),
    onSurfaceVariant = Graphite,
    outline = Hairline,
    inverseSurface = Ink,
    inverseOnSurface = Paper,
)

private val DarkScheme = darkColorScheme(
    primary = Chalk,
    onPrimary = Night,
    background = Night,
    onBackground = Chalk,
    surface = Night,
    onSurface = Chalk,
    surfaceVariant = Color(0xFF141416),
    onSurfaceVariant = Ash,
    outline = NightHairline,
    inverseSurface = Chalk,
    inverseOnSurface = Night,
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
    ),
    headlineLarge = TextStyle(
        fontFamily = SlateFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp,
        lineHeight = 35.sp,
        letterSpacing = (-0.9).sp,
    ),
    titleMedium = TextStyle(
        fontFamily = SlateFont,
        fontWeight = FontWeight.Medium,
        fontSize = 17.sp,
        lineHeight = 23.sp,
        letterSpacing = (-0.2).sp,
    ),
    titleSmall = TextStyle(
        fontFamily = SlateFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 19.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = SlateFont,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 23.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = SlateFont,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 19.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = SlateFont,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = SlateFont,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.1.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = SlateFont,
        fontWeight = FontWeight.Medium,
        fontSize = 9.sp,
        lineHeight = 12.sp,
        letterSpacing = 0.2.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = SlateFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp,
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
