package com.slate.weekly.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.slate.weekly.R
import com.slate.weekly.data.SlateSettings
import com.slate.weekly.data.ThemeMode
import com.slate.weekly.domain.focusEnd
import com.slate.weekly.domain.isFocusActive
import com.slate.weekly.ui.components.AddPill
import com.slate.weekly.ui.components.Chevron
import com.slate.weekly.ui.components.ChevronDirection
import com.slate.weekly.ui.components.SectionLabel
import com.slate.weekly.ui.components.SlateCard
import com.slate.weekly.ui.components.Stepper
import com.slate.weekly.ui.components.TextAction
import com.slate.weekly.ui.components.Toggle
import com.slate.weekly.util.formatTime
import com.slate.weekly.util.monthName
import java.time.Instant
import java.time.ZoneId

@Composable
fun SettingsScreen(
    settings: SlateSettings,
    versionName: String,
    notificationsBlocked: Boolean,
    focusServiceEnabled: Boolean,
    onTheme: (ThemeMode) -> Unit,
    onReminder: (Boolean, Int, Int) -> Unit,
    onPreviewReminder: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onOpenFocusApps: () -> Unit,
    onStartFocus: (Int) -> Unit,
    onOpenAccessibilitySettings: () -> Unit,
    onResetHabits: () -> Unit,
) {
    var timeOpen by rememberSaveable { mutableStateOf(false) }
    var resetOpen by rememberSaveable { mutableStateOf(false) }
    var focusHours by rememberSaveable { mutableIntStateOf(4) }
    var confirmHours by rememberSaveable { mutableIntStateOf(0) }
    val focusActive = isFocusActive(settings.focusUntil, System.currentTimeMillis())
    val focusCount = settings.blockedPackages.size

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.settings),
            style = MaterialTheme.typography.headlineLarge,
        )

        Spacer(Modifier.height(20.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 132.dp),
        ) {
            item(contentType = "appearance") {
                SlateCard {
                    SectionLabel(stringResource(R.string.appearance))
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.selectableGroup(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        ChoicePill(
                            label = stringResource(R.string.light),
                            selected = settings.theme == ThemeMode.Light,
                            modifier = Modifier.weight(1f),
                            onClick = { onTheme(ThemeMode.Light) },
                        )
                        ChoicePill(
                            label = stringResource(R.string.dark),
                            selected = settings.theme == ThemeMode.Dark,
                            modifier = Modifier.weight(1f),
                            onClick = { onTheme(ThemeMode.Dark) },
                        )
                        ChoicePill(
                            label = stringResource(R.string.auto),
                            selected = settings.theme == ThemeMode.Auto,
                            modifier = Modifier.weight(1f),
                            onClick = { onTheme(ThemeMode.Auto) },
                        )
                    }
                }
            }

            item(contentType = "reminder") {
                SlateCard(contentPadding = PaddingValues(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.reminder),
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.weight(1f),
                        )
                        Toggle(checked = settings.reminderOn) {
                            onReminder(it, settings.reminderHour, settings.reminderMinute)
                        }
                    }
                    if (settings.reminderOn) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(end = 8.dp)
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant),
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { timeOpen = true }
                                .padding(end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(R.string.remind_at),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                text = formatTime(settings.reminderHour, settings.reminderMinute),
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                        if (notificationsBlocked) {
                            Text(
                                text = stringResource(R.string.notifications_off),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(end = 8.dp),
                            )
                            TextAction(
                                text = stringResource(R.string.open_settings),
                                onClick = onOpenNotificationSettings,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 8.dp),
                                contentAlignment = Alignment.CenterEnd,
                            ) {
                                TextAction(
                                    text = stringResource(R.string.send_test),
                                    onClick = onPreviewReminder,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }
                    }
                }
            }

            item(contentType = "focus") {
                SlateCard {
                    SectionLabel(stringResource(R.string.focus))
                    Spacer(Modifier.height(12.dp))
                    if (focusActive) {
                        Text(
                            text = focusEndLabel(settings.focusUntil, System.currentTimeMillis()),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = pluralStringResource(
                                R.plurals.focus_apps_blocked,
                                focusCount,
                                focusCount,
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.focus_strict_note),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (!focusServiceEnabled) {
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = stringResource(R.string.focus_service_off),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            TextAction(
                                text = stringResource(R.string.turn_on),
                                onClick = onOpenAccessibilitySettings,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable(onClick = onOpenFocusApps),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(R.string.blocked_apps),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                text = if (focusCount == 0) {
                                    stringResource(R.string.choose_apps)
                                } else {
                                    pluralStringResource(
                                        R.plurals.apps_n,
                                        focusCount,
                                        focusCount,
                                    )
                                },
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Spacer(Modifier.width(8.dp))
                            Chevron(
                                direction = ChevronDirection.Right,
                                modifier = Modifier.size(14.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            )
                        }
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant),
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.focus_timer),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FocusDurations.forEach { hours ->
                                ChoicePill(
                                    label = stringResource(durationLabel(hours)),
                                    selected = focusHours == hours,
                                    modifier = Modifier.weight(1f),
                                    onClick = { focusHours = hours },
                                )
                            }
                        }
                        if (!focusServiceEnabled) {
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = stringResource(R.string.focus_service_off),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            TextAction(
                                text = stringResource(R.string.turn_on),
                                onClick = onOpenAccessibilitySettings,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        AddPill(
                            text = stringResource(R.string.focus_start),
                            onClick = { confirmHours = focusHours },
                            enabled = focusCount > 0 && focusServiceEnabled,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.focus_strict_note),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            item(contentType = "data") {
                SlateCard {
                    SectionLabel(stringResource(R.string.data))
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { resetOpen = true },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.reset_all),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            item(contentType = "about") {
                SlateCard {
                    SectionLabel(stringResource(R.string.about))
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.about_text),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.version, versionName),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }

    if (timeOpen) {
        TimeDialog(
            hour = settings.reminderHour,
            minute = settings.reminderMinute,
            onDismiss = { timeOpen = false },
            onSave = { hour, minute ->
                timeOpen = false
                onReminder(true, hour, minute)
            },
        )
    }

    if (resetOpen) {
        AlertDialog(
            onDismissRequest = { resetOpen = false },
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            title = {
                Text(
                    text = stringResource(R.string.reset_title),
                    style = MaterialTheme.typography.titleMedium,
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.reset_body),
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        resetOpen = false
                        onResetHabits()
                    },
                ) {
                    Text(
                        text = stringResource(R.string.reset),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { resetOpen = false }) {
                    Text(
                        text = stringResource(R.string.cancel),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
        )
    }

    if (confirmHours > 0) {
        val hours = confirmHours
        val nowMillis = System.currentTimeMillis()
        val endLabel = focusEndLabel(focusEnd(nowMillis, hours), nowMillis)
        AlertDialog(
            onDismissRequest = { confirmHours = 0 },
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            title = {
                Text(
                    text = stringResource(R.string.focus_confirm_title),
                    style = MaterialTheme.typography.titleMedium,
                )
            },
            text = {
                Text(
                    text = stringResource(
                        R.string.focus_confirm_body,
                        pluralStringResource(R.plurals.apps_n, focusCount, focusCount),
                        endLabel,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmHours = 0
                        onStartFocus(hours)
                    },
                ) {
                    Text(
                        text = stringResource(R.string.focus_start),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmHours = 0 }) {
                    Text(
                        text = stringResource(R.string.cancel),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
        )
    }
}

@Composable
private fun focusEndLabel(untilMillis: Long, nowMillis: Long): String {
    val zone = ZoneId.systemDefault()
    val now = Instant.ofEpochMilli(nowMillis).atZone(zone)
    val end = Instant.ofEpochMilli(untilMillis).atZone(zone)
    val time = formatTime(end.hour, end.minute)
    return when (end.toLocalDate()) {
        now.toLocalDate() -> stringResource(R.string.focus_until, time)
        now.toLocalDate().plusDays(1) -> stringResource(R.string.focus_until_tomorrow, time)
        else -> stringResource(
            R.string.focus_until_date,
            end.dayOfMonth,
            monthName(end.month, true),
            time,
        )
    }
}

private val FocusDurations = listOf(1, 4, 8, 24)

private fun durationLabel(hours: Int): Int = when (hours) {
    1 -> R.string.duration_1h
    4 -> R.string.duration_4h
    8 -> R.string.duration_8h
    else -> R.string.duration_24h
}

@Composable
private fun ChoicePill(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val background by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.onSurface
        } else {
            Color.Transparent
        },
        animationSpec = tween(180),
        label = "themeBackground",
    )
    val border by animateColorAsState(
        targetValue = if (selected) {
            Color.Transparent
        } else {
            MaterialTheme.colorScheme.outlineVariant
        },
        animationSpec = tween(180),
        label = "themeBorder",
    )
    val foreground by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.background
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(180),
        label = "themeForeground",
    )

    Box(
        modifier = modifier
            .height(48.dp)
            .clip(CircleShape)
            .background(background)
            .border(1.dp, border, CircleShape)
            .selectable(
                selected = selected,
                role = Role.RadioButton,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = foreground,
        )
    }
}

@Composable
private fun TimeDialog(
    hour: Int,
    minute: Int,
    onDismiss: () -> Unit,
    onSave: (Int, Int) -> Unit,
) {
    var hours by rememberSaveable { mutableIntStateOf(hour) }
    var minutes by rememberSaveable { mutableIntStateOf(minute) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface,
        title = {
            Text(
                text = stringResource(R.string.remind_at),
                style = MaterialTheme.typography.titleMedium,
            )
        },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.hour),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f),
                    )
                    Stepper(value = hours, onChange = { hours = it }, min = 0, max = 23, pad = true)
                }
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.minute),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f),
                    )
                    Stepper(value = minutes, onChange = { minutes = it }, min = 0, max = 55, step = 5, pad = true)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(hours, minutes) }) {
                Text(
                    text = stringResource(R.string.save),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
    )
}
