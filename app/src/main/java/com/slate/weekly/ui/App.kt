package com.slate.weekly.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.currentBackStackEntryAsState
import com.slate.weekly.BuildConfig
import com.slate.weekly.R
import com.slate.weekly.data.Habit
import com.slate.weekly.data.ThemeMode
import com.slate.weekly.focus.FocusAccess
import com.slate.weekly.ui.theme.SlateTheme
import com.slate.weekly.util.coerceWeek
import com.slate.weekly.util.startOfWeek
import com.slate.weekly.ui.SlateRoutes
import java.time.LocalDate

@Composable
fun SlateRoot(viewModel: SlateViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    var addOpen by rememberSaveable { mutableStateOf(false) }
    var welcomeDismissed by rememberSaveable { mutableStateOf(false) }
    var weekStart by rememberSaveable(stateSaver = LocalDateSaver) {
        mutableStateOf(state.today.startOfWeek())
    }
    var lastToday by rememberSaveable(stateSaver = LocalDateSaver) {
        mutableStateOf(state.today)
    }
    var notificationsAllowed by remember { mutableStateOf(notificationsEnabled(context)) }
    var focusServiceEnabled by remember { mutableStateOf(FocusAccess.isServiceEnabled(context)) }

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
                focusServiceEnabled = FocusAccess.isServiceEnabled(context)
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
        val firstWeek = state.habits
            .minOfOrNull { LocalDate.ofEpochDay(it.createdDay).startOfWeek() }
            ?: thisWeek
        weekStart = coerceWeek(weekStart, firstWeek, thisWeek)
    }

    BackHandler(enabled = navController.currentBackStackEntryCount > 1 || addOpen) {
        if (addOpen) {
            addOpen = false
        } else {
            navController.popBackStack()
        }
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

            NavHost(
                navController = navController,
                startDestination = SlateRoutes.Home,
            ) {
                composable(SlateRoutes.Home) {
                    HomeScreen(
                        habits = state.habits,
                        weekStart = weekStart,
                        today = state.today,
                        onShiftWeek = { delta ->
                            val thisWeek = state.today.startOfWeek()
                            val firstWeek = state.habits
                                .minOfOrNull {
                                    LocalDate.ofEpochDay(it.createdDay).startOfWeek()
                                }
                                ?: thisWeek
                            weekStart = coerceWeek(
                                weekStart.plusWeeks(delta),
                                firstWeek,
                                thisWeek,
                            )
                        },
                        onThisWeek = { weekStart = state.today.startOfWeek() },
                        onToggle = viewModel::toggle,
                        onOpen = { id -> navController.navigate("${SlateRoutes.Detail.replace("{habitId}", id)}") },
                        onAdd = { addOpen = true },
                    )
                }
                composable(SlateRoutes.Detail) { backStackEntry ->
                    val habitId = backStackEntry.arguments?.getString("habitId") ?: ""
                    val habit = state.habits.firstOrNull { it.id == habitId }
                    if (habit != null) {
                        HabitScreen(
                            habit = habit,
                            today = state.today,
                            initialWeek = weekStart,
                            onBack = { navController.popBackStack() },
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
                                navController.popBackStack()
                            },
                        )
                    } else {
                        LaunchedEffect(habitId) {
                            navController.popBackStack()
                        }
                    }
                }
                composable(SlateRoutes.Year) {
                    YearScreen(
                        habits = state.habits,
                        today = state.today,
                        onAdd = {
                            addOpen = true
                        },
                    )
                }
                composable(SlateRoutes.Settings) {
                    SettingsScreen(
                        settings = state.settings,
                        versionName = BuildConfig.VERSION_NAME,
                        notificationsBlocked = !notificationsAllowed,
                        focusServiceEnabled = focusServiceEnabled,
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
                        onPreviewReminder = viewModel::previewReminder,
                        onOpenNotificationSettings = {
                            openNotificationSettings(context)
                        },
                        onOpenFocusApps = { navController.navigate(SlateRoutes.FocusApps) },
                        onStartFocus = viewModel::startFocus,
                        onOpenAccessibilitySettings = {
                            FocusAccess.openAccessibilitySettings(context)
                        },
                        onResetHabits = {
                            viewModel.resetHabits()
                            weekStart = state.today.startOfWeek()
                            navController.navigate(SlateRoutes.Home) {
                                popUpTo(SlateRoutes.Home) {
                                    inclusive = true
                                }
                            }
                        },
                    )
                }
                composable(SlateRoutes.FocusApps) {
                    FocusAppsScreen(
                        selected = state.settings.blockedPackages,
                        onToggle = viewModel::setBlockedApp,
                        onBack = { navController.popBackStack() },
                    )
                }
            }

            val currentBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = currentBackStackEntry?.destination?.route ?: SlateRoutes.Home

            SlateBottomBar(
                navController = navController,
                currentRoute = currentRoute,
                modifier = Modifier
                    .align(Alignment.BottomCenter),
            )

            AnimatedVisibility(
                visible = addOpen,
                modifier = Modifier.fillMaxSize(),
                enter = fadeIn(tween(160)) + slideInVertically(tween(240)) { it },
                exit = fadeOut(tween(120)) + slideOutVertically(tween(180)) { it },
            ) {
                AddHabitSheet(
                    onDismiss = { addOpen = false },
                    onAdd = { name, target ->
                        viewModel.add(name, target)
                        weekStart = state.today.startOfWeek()
                        addOpen = false
                    },
                )
            }

            val showWelcome = state.loaded &&
                !state.welcomeSeen &&
                !welcomeDismissed
            AnimatedVisibility(
                visible = showWelcome,
                modifier = Modifier.fillMaxSize(),
                enter = fadeIn(tween(420)),
                exit = fadeOut(tween(280)),
            ) {
                Box(Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                awaitPointerEventScope {
                                    while (true) {
                                        awaitPointerEvent()
                                    }
                                }
                            },
                    )
                    WelcomeScreen(
                        onBegin = {
                            welcomeDismissed = true
                            viewModel.markWelcomeSeen()
                        },
                    )
                }
            }
        }
    }
}
