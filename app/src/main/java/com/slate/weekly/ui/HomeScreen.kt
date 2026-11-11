package com.slate.weekly.ui

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.slate.weekly.R
import com.slate.weekly.data.Habit
import com.slate.weekly.util.dayLabel
import com.slate.weekly.util.startOfWeek
import com.slate.weekly.util.weekDays
import com.slate.weekly.util.weekRangeLabel
import java.time.LocalDate

@Composable
fun HomeScreen(
    habits: List<Habit>,
    weekStart: LocalDate,
    today: LocalDate,
    onShiftWeek: (Long) -> Unit,
    onThisWeek: () -> Unit,
    onToggle: (String, LocalDate) -> Unit,
    onOpen: (String) -> Unit,
    onYear: () -> Unit,
    onSettings: () -> Unit,
    onAdd: () -> Unit,
) {
    val active = remember(habits) { habits.filter { !it.archived } }
    val week = remember(weekStart) { weekDays(weekStart) }
    val isThisWeek = weekStart == today.startOfWeek()
    val earliestWeek = remember(active) {
        active.minOfOrNull { LocalDate.ofEpochDay(it.createdDay).startOfWeek() }
    }
    val done = active.sumOf { habit -> week.count { it.toEpochDay() in habit.checks } }
    val target = active.sumOf { it.target }

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleMedium,
                )
                Row {
                    SmallAction(stringResource(R.string.year), onYear)
                    Spacer(Modifier.width(2.dp))
                    SmallAction(stringResource(R.string.settings), onSettings)
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                ArrowText("‹", enabled = earliestWeek == null || weekStart > earliestWeek) { onShiftWeek(-1) }
                Column(
                    Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp),
                ) {
                    Text(
                        text = weekRangeLabel(weekStart),
                        style = MaterialTheme.typography.headlineLarge,
                    )
                    if (active.isNotEmpty()) {
                        Spacer(Modifier.height(5.dp))
                        Text(
                            text = stringResource(R.string.week_progress, done, target),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                ArrowText("›", enabled = !isThisWeek) { onShiftWeek(1) }
            }

            if (!isThisWeek) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.back_to_this_week),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(onClick = onThisWeek)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                )
            }

            Spacer(Modifier.height(16.dp))

            if (active.isEmpty()) {
                EmptyState(onAdd)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 108.dp),
                ) {
                    items(active, key = { it.id }) { habit ->
                        HabitCard(
                            habit = habit,
                            week = week,
                            today = today,
                            onToggle = onToggle,
                            onOpen = onOpen,
                        )
                    }
                }
            }
        }

        if (active.isNotEmpty()) {
            AddPill(
                text = stringResource(R.string.add),
                onClick = onAdd,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun EmptyState(onAdd: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 72.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.nothing_yet),
            style = MaterialTheme.typography.headlineLarge,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.nothing_yet_sub),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(26.dp))
        AddPill(
            text = stringResource(R.string.add),
            onClick = onAdd,
            modifier = Modifier.width(190.dp),
        )
    }
}

@Composable
private fun HabitCard(
    habit: Habit,
    week: List<LocalDate>,
    today: LocalDate,
    onToggle: (String, LocalDate) -> Unit,
    onOpen: (String) -> Unit,
) {
    val shape = RoundedCornerShape(22.dp)
    val done = week.count { it.toEpochDay() in habit.checks }
    val met = done >= habit.target

    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onOpen(habit.id) }
            .border(1.dp, MaterialTheme.colorScheme.outline, shape)
            .padding(horizontal = 16.dp, vertical = 15.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = habit.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "$done/${habit.target}",
                style = MaterialTheme.typography.labelMedium,
                color = if (met) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
        }
        Spacer(Modifier.height(10.dp))
        WeekStrip(
            days = week,
            checks = habit.checks,
            today = today,
            onToggle = { onToggle(habit.id, it) },
        )
    }
}

@Composable
fun WeekStrip(
    days: List<LocalDate>,
    checks: Set<Long>,
    today: LocalDate,
    onToggle: (LocalDate) -> Unit,
    cell: Dp = 32.dp,
) {
    val haptics = LocalHapticFeedback.current
    Row(Modifier.fillMaxWidth()) {
        days.forEach { day ->
            val done = day.toEpochDay() in checks
            val enabled = day <= today
            val isToday = day == today

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = dayLabel(day),
                    style = MaterialTheme.typography.labelSmall,
                    color = when {
                        isToday -> MaterialTheme.colorScheme.onSurface
                        enabled -> MaterialTheme.colorScheme.onSurfaceVariant
                        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                    },
                )
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .size(cell + 8.dp)
                        .clip(CircleShape)
                        .then(
                            if (enabled) {
                                Modifier.toggleable(
                                    value = done,
                                    role = Role.Checkbox,
                                    onValueChange = {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onToggle(day)
                                    },
                                )
                            } else {
                                Modifier
                            },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(cell)
                            .clip(CircleShape)
                            .background(
                                if (done) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                            )
                            .then(
                                if (done) {
                                    Modifier
                                } else {
                                    Modifier.border(
                                        width = if (isToday) 1.5.dp else 1.dp,
                                        color = when {
                                            !enabled -> MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)
                                            isToday -> MaterialTheme.colorScheme.onSurface
                                            else -> MaterialTheme.colorScheme.outline
                                        },
                                        shape = CircleShape,
                                    )
                                },
                            ),
                    )
                }
            }
        }
    }
}
