package com.slate.weekly.ui

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import java.time.LocalDate

internal sealed interface Screen {
    data object Home : Screen
    data class Detail(val id: String) : Screen
    data object Year : Screen
    data object Settings : Screen
    data object FocusApps : Screen
}

internal enum class MainTab { Habits, Year, Settings }

internal fun MainTab.screen(): Screen = when (this) {
    MainTab.Habits -> Screen.Home
    MainTab.Year -> Screen.Year
    MainTab.Settings -> Screen.Settings
}

internal fun Screen.tab(): MainTab? = when (this) {
    Screen.Home -> MainTab.Habits
    Screen.Year -> MainTab.Year
    Screen.Settings -> MainTab.Settings
    is Screen.Detail -> null
    Screen.FocusApps -> null
}

internal fun Screen.back(): Screen = when (this) {
    Screen.FocusApps -> Screen.Settings
    else -> Screen.Home
}

internal val ScreenSaver = listSaver<Screen, Any>(
    save = { screen ->
        when (screen) {
            Screen.Home -> listOf("home")
            Screen.Year -> listOf("year")
            Screen.Settings -> listOf("settings")
            Screen.FocusApps -> listOf("focus_apps")
            is Screen.Detail -> listOf("detail", screen.id)
        }
    },
    restore = { values ->
        when (values.firstOrNull()) {
            "year" -> Screen.Year
            "settings" -> Screen.Settings
            "focus_apps" -> Screen.FocusApps
            "detail" -> Screen.Detail(values.getOrNull(1) as? String ?: "")
            else -> Screen.Home
        }
    },
)

internal val LocalDateSaver = Saver<LocalDate, Long>(
    save = { it.toEpochDay() },
    restore = { LocalDate.ofEpochDay(it) },
)
