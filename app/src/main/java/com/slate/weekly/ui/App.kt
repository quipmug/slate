package com.slate.weekly.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.slate.weekly.BuildConfig
import com.slate.weekly.data.SlateSettings
import com.slate.weekly.data.SlateStore
import com.slate.weekly.reminder.Reminders
import com.slate.weekly.ui.theme.SlateTheme
import com.slate.weekly.util.startOfWeek
import java.time.LocalDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private sealed interface Screen {
    data object Home : Screen
    data class Detail(val id: String) : Screen
    data object Year : Screen
    data object Settings : Screen
}

private val ScreenSaver = listSaver<Screen, Any>(
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

@Composable
fun SlateRoot(store: SlateStore) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    val habits by store.habits.collectAsStateWithLifecycle(initialValue = emptyList())
    val settings by store.settings.collectAsStateWithLifecycle(initialValue = SlateSettings())

    var screen by rememberSaveable(stateSaver = ScreenSaver) {
        mutableStateOf<Screen>(Screen.Home)
    }
    var addOpen by rememberSaveable { mutableStateOf(false) }
    var today by remember { mutableStateOf(LocalDate.now()) }
    var lastSeenDay by rememberSaveable(stateSaver = LocalDateSaver) {
        mutableStateOf(LocalDate.now())
    }
    var weekStart by rememberSaveable(stateSaver = LocalDateSaver) {
        mutableStateOf(LocalDate.now().startOfWeek())
    }
    var notificationsAllowed by remember { mutableStateOf(notificationsEnabled(context)) }

    val dark = when (settings.theme) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { notificationsAllowed = notificationsEnabled(context) },
    )

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                notificationsAllowed = notificationsEnabled(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        if (lastSeenDay != today) {
            val wasCurrent = weekStart == lastSeenDay.startOfWeek()
            if (wasCurrent) weekStart = today.startOfWeek()
            lastSeenDay = today
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000)
            val now = LocalDate.now()
            if (now != today) {
                val wasCurrent = weekStart == today.startOfWeek()
                today = now
                if (wasCurrent) weekStart = now.startOfWeek()
                lastSeenDay = now
            }
        }
    }

    LaunchedEffect(today) {
        if (weekStart > today.startOfWeek()) weekStart = today.startOfWeek()
    }

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
                                initialWeek = weekStart,
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
                                    if (habits.size <= 1) weekStart = today.startOfWeek()
                                    screen = Screen.Home
                                },
                            )
                        } else if (habits.isNotEmpty()) {
                            LaunchedEffect(target.id) { screen = Screen.Home }
                        }
                    }

                    Screen.Year -> YearScreen(
                        habits = habits,
                        today = today,
                        onBack = { screen = Screen.Home },
                        onAdd = {
                            screen = Screen.Home
                            addOpen = true
                        },
                    )

                    Screen.Settings -> SettingsScreen(
                        settings = settings,
                        versionName = BuildConfig.VERSION_NAME,
                        notificationsBlocked = !notificationsAllowed,
                        onBack = { screen = Screen.Home },
                        onTheme = { theme ->
                            scope.launch { store.setTheme(theme) }
                        },
                        onReminder = { enabled, hour, minute ->
                            scope.launch { store.setReminder(enabled, hour, minute) }
                            if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                !notificationsAllowed
                            ) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        },
                        onOpenNotificationSettings = {
                            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            runCatching { context.startActivity(intent) }.onFailure {
                                val fallback = Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", context.packageName, null),
                                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                runCatching { context.startActivity(fallback) }
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
                        weekStart = today.startOfWeek()
                        addOpen = false
                    },
                )
            }
        }
    }
}

private fun notificationsEnabled(context: Context): Boolean {
    val enabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
    val permission = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    return enabled && permission
}
