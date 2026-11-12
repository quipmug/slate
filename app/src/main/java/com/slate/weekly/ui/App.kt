package com.slate.weekly.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.slate.weekly.BuildConfig
import com.slate.weekly.data.ThemeMode
import com.slate.weekly.ui.theme.SlateTheme
import com.slate.weekly.util.startOfWeek
import java.time.LocalDate

@Composable
fun SlateRoot(viewModel: SlateViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var screen by rememberSaveable(stateSaver = ScreenSaver) {
        mutableStateOf<Screen>(Screen.Home)
    }
    var addOpen by rememberSaveable { mutableStateOf(false) }
    var weekStart by rememberSaveable(stateSaver = LocalDateSaver) {
        mutableStateOf(state.today.startOfWeek())
    }
    var lastToday by rememberSaveable(stateSaver = LocalDateSaver) {
        mutableStateOf(state.today)
    }
    var notificationsAllowed by remember { mutableStateOf(notificationsEnabled(context)) }

    val dark = when (state.settings.theme) {
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
        ThemeMode.Auto -> isSystemInDarkTheme()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { notificationsAllowed = notificationsEnabled(context) },
    )

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshToday()
                notificationsAllowed = notificationsEnabled(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(state.today) {
        val today = state.today
        if (today != lastToday) {
            if (weekStart == lastToday.startOfWeek()) weekStart = today.startOfWeek()
            lastToday = today
        }
        if (weekStart > today.startOfWeek()) weekStart = today.startOfWeek()
    }

    LaunchedEffect(state.loaded, state.habits, state.today) {
        if (!state.loaded) return@LaunchedEffect
        val thisWeek = state.today.startOfWeek()
        val earliestWeek = state.habits
            .minOfOrNull { LocalDate.ofEpochDay(it.createdDay).startOfWeek() }
        weekStart = when {
            earliestWeek == null -> thisWeek
            weekStart < earliestWeek -> earliestWeek
            weekStart > thisWeek -> thisWeek
            else -> weekStart
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
            if (!state.loaded) {
                return@Box
            }

            AnimatedContent(
                targetState = screen,
                modifier = Modifier.fillMaxSize(),
                transitionSpec = {
                    (fadeIn(tween(200)) + slideInVertically(tween(280)) { it / 28 }) togetherWith
                        (fadeOut(tween(120)) + slideOutVertically(tween(200)) { -it / 28 })
                },
                label = "screen",
            ) { target ->
                when (target) {
                    Screen.Home -> HomeScreen(
                        habits = state.habits,
                        weekStart = weekStart,
                        today = state.today,
                        onShiftWeek = { delta ->
                            val next = weekStart.plusWeeks(delta)
                            if (next <= state.today.startOfWeek()) weekStart = next
                        },
                        onThisWeek = { weekStart = state.today.startOfWeek() },
                        onToggle = viewModel::toggle,
                        onOpen = { id -> screen = Screen.Detail(id) },
                        onYear = { screen = Screen.Year },
                        onSettings = { screen = Screen.Settings },
                        onAdd = { addOpen = true },
                    )

                    is Screen.Detail -> {
                        val habit = state.habits.firstOrNull { it.id == target.id }
                        if (habit != null) {
                            HabitScreen(
                                habit = habit,
                                today = state.today,
                                initialWeek = weekStart,
                                onBack = { screen = Screen.Home },
                                onToggle = { day -> viewModel.toggle(habit.id, day) },
                                onRename = { name -> viewModel.rename(habit.id, name) },
                                onSetTarget = { targetValue ->
                                    viewModel.setTarget(habit.id, targetValue)
                                },
                                onDelete = {
                                    viewModel.delete(habit.id)
                                    if (state.habits.size <= 1) {
                                        weekStart = state.today.startOfWeek()
                                    }
                                    screen = Screen.Home
                                },
                            )
                        } else {
                            LaunchedEffect(target.id) {
                                screen = Screen.Home
                            }
                        }
                    }

                    Screen.Year -> YearScreen(
                        habits = state.habits,
                        today = state.today,
                        onBack = { screen = Screen.Home },
                        onAdd = {
                            screen = Screen.Home
                            addOpen = true
                        },
                    )

                    Screen.Settings -> SettingsScreen(
                        settings = state.settings,
                        versionName = BuildConfig.VERSION_NAME,
                        notificationsBlocked = !notificationsAllowed,
                        onBack = { screen = Screen.Home },
                        onTheme = viewModel::setTheme,
                        onReminder = { enabled, hour, minute ->
                            viewModel.setReminder(enabled, hour, minute)
                            if (enabled &&
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                !notificationsAllowed
                            ) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        },
                        onOpenNotificationSettings = {
                            openNotificationSettings(context)
                        },
                        onResetHabits = {
                            viewModel.resetHabits()
                            weekStart = state.today.startOfWeek()
                            screen = Screen.Home
                        },
                    )
                }
            }

            if (addOpen) {
                AddHabitSheet(
                    onDismiss = { addOpen = false },
                    onAdd = { name, target ->
                        viewModel.add(name, target)
                        weekStart = state.today.startOfWeek()
                        screen = Screen.Home
                        addOpen = false
                    },
                )
            }
        }
    }
}
