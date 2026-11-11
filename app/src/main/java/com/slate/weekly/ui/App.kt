package com.slate.weekly.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.slate.weekly.BuildConfig
import com.slate.weekly.data.SlateSettings
import com.slate.weekly.data.SlateStore
import com.slate.weekly.reminder.Reminders
import com.slate.weekly.ui.theme.SlateTheme
import com.slate.weekly.util.startOfWeek
import java.time.LocalDate
import kotlinx.coroutines.launch

private sealed interface Screen {
    data object Home : Screen
    data class Detail(val id: String) : Screen
    data object Year : Screen
    data object Settings : Screen
}

@Composable
fun SlateRoot(store: SlateStore) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val habits by store.habits.collectAsStateWithLifecycle(initialValue = emptyList())
    val settings by store.settings.collectAsStateWithLifecycle(initialValue = SlateSettings())

    var screen by remember { mutableStateOf<Screen>(Screen.Home) }
    var addOpen by remember { mutableStateOf(false) }
    var weekStart by remember { mutableStateOf(LocalDate.now().startOfWeek()) }

    val today = LocalDate.now()
    val dark = when (settings.theme) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { },
    )

    LaunchedEffect(settings.reminderOn, settings.reminderHour, settings.reminderMinute) {
        if (settings.reminderOn) {
            Reminders.schedule(context, settings.reminderHour, settings.reminderMinute)
        } else {
            Reminders.cancel(context)
        }
    }

    BackHandler(enabled = screen != Screen.Home && !addOpen) {
        screen = Screen.Home
    }

    SlateTheme(dark = dark) {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            Crossfade(
                targetState = screen,
                animationSpec = tween(220),
                label = "screen",
            ) { target ->
                when (target) {
                    Screen.Home -> HomeScreen(
                        habits = habits,
                        weekStart = weekStart,
                        today = today,
                        onShiftWeek = { delta ->
                            val next = weekStart.plusWeeks(delta)
                            if (next <= today.startOfWeek()) weekStart = next
                        },
                        onThisWeek = { weekStart = today.startOfWeek() },
                        onToggle = { id, day ->
                            scope.launch { store.toggle(id, day.toEpochDay()) }
                        },
                        onOpen = { id -> screen = Screen.Detail(id) },
                        onYear = { screen = Screen.Year },
                        onSettings = { screen = Screen.Settings },
                        onAdd = { addOpen = true },
                    )

                    is Screen.Detail -> {
                        val habit = habits.firstOrNull { it.id == target.id }
                        if (habit != null) {
                            HabitScreen(
                                habit = habit,
                                today = today,
                                onBack = { screen = Screen.Home },
                                onToggle = { day ->
                                    scope.launch { store.toggle(habit.id, day.toEpochDay()) }
                                },
                                onRename = { name ->
                                    scope.launch { store.rename(habit.id, name) }
                                },
                                onSetTarget = { targetValue ->
                                    scope.launch { store.setTarget(habit.id, targetValue) }
                                },
                                onDelete = {
                                    scope.launch { store.delete(habit.id) }
                                    screen = Screen.Home
                                },
                            )
                        } else {
                            Box(Modifier.fillMaxSize())
                        }
                    }

                    Screen.Year -> YearScreen(
                        habits = habits,
                        today = today,
                        onBack = { screen = Screen.Home },
                    )

                    Screen.Settings -> SettingsScreen(
                        settings = settings,
                        versionName = BuildConfig.VERSION_NAME,
                        onBack = { screen = Screen.Home },
                        onTheme = { theme ->
                            scope.launch { store.setTheme(theme) }
                        },
                        onReminder = { enabled, hour, minute ->
                            scope.launch { store.setReminder(enabled, hour, minute) }
                            if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                val granted = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS,
                                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                                if (!granted) {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            }
                        },
                    )
                }
            }

            if (addOpen) {
                AddHabitSheet(
                    onDismiss = { addOpen = false },
                    onAdd = { name, target ->
                        scope.launch { store.add(name, target) }
                        addOpen = false
                    },
                )
            }
        }
    }
}
