package com.slate.weekly.ui

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import java.time.LocalDate

internal sealed interface Screen {
    data object Home : Screen
    data class Detail(val id: String) : Screen
    data object Year : Screen
    data object Settings : Screen
}

internal val ScreenSaver = listSaver<Screen, Any>(
    save = { screen ->
        when (screen) {
            Screen.Home -> listOf("home")
            Screen.Year -> listOf("year")
            Screen.Settings -> listOf("settings")
            is Screen.Detail -> listOf("detail", screen.id)
        }
    },
    restore = { values ->
        when (values.firstOrNull()) {
            "year" -> Screen.Year
            "settings" -> Screen.Settings
            "detail" -> Screen.Detail(values.getOrNull(1) as? String ?: "")
            else -> Screen.Home
        }
    },
)

internal val LocalDateSaver = Saver<LocalDate, Long>(
    save = { it.toEpochDay() },
    restore = { LocalDate.ofEpochDay(it) },
)
