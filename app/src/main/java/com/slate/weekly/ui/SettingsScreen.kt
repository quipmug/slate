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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.slate.weekly.R
import com.slate.weekly.data.SlateSettings
import com.slate.weekly.util.formatTime

@Composable
fun SettingsScreen(
    settings: SlateSettings,
    versionName: String,
    notificationsBlocked: Boolean,
    onBack: () -> Unit,
    onTheme: (String) -> Unit,
    onReminder: (Boolean, Int, Int) -> Unit,
    onOpenNotificationSettings: () -> Unit,
) {
    var timeOpen by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(20.dp)

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(4.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            SmallAction(
                text = "‹ ${stringResource(R.string.back)}",
                onClick = onBack,
                description = stringResource(R.string.back),
            )
        }

        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.settings),
            style = MaterialTheme.typography.headlineLarge,
        )

        Spacer(Modifier.height(20.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape)
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline, shape)
                        .padding(16.dp),
                ) {
                    SectionLabel(stringResource(R.string.appearance))
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ThemeChoice(
                            label = stringResource(R.string.light),
                            selected = settings.theme == "light",
                            modifier = Modifier.weight(1f),
                            onClick = { onTheme("light") },
                        )
                        ThemeChoice(
                            label = stringResource(R.string.dark),
                            selected = settings.theme == "dark",
                            modifier = Modifier.weight(1f),
                            onClick = { onTheme("dark") },
                        )
                        ThemeChoice(
                            label = stringResource(R.string.auto),
                            selected = settings.theme == "auto",
                            modifier = Modifier.weight(1f),
                            onClick = { onTheme("auto") },
                        )
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape)
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline, shape)
                        .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                ) {
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
                                .clip(CircleShape)
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
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape)
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline, shape)
                        .padding(16.dp),
                ) {
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
}

@Composable
private fun ThemeChoice(
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
            MaterialTheme.colorScheme.outline
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
    var hours by remember { mutableIntStateOf(hour) }
    var minutes by remember { mutableIntStateOf(minute) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
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
