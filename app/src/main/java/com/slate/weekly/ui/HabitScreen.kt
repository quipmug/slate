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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.slate.weekly.R
import com.slate.weekly.data.Habit
import com.slate.weekly.util.startOfWeek
import com.slate.weekly.util.weekChecks
import com.slate.weekly.util.weekDays
import com.slate.weekly.util.weekRangeLabel
import com.slate.weekly.util.weeksInARow
import java.time.LocalDate

@Composable
fun HabitScreen(
    habit: Habit,
    today: LocalDate,
    onBack: () -> Unit,
    onToggle: (LocalDate) -> Unit,
    onRename: (String) -> Unit,
    onSetTarget: (Int) -> Unit,
    onDelete: () -> Unit,
) {
    val thisWeek = today.startOfWeek()
    val firstWeek = remember(habit.createdDay) {
        LocalDate.ofEpochDay(habit.createdDay).startOfWeek()
    }
    var weekStart by remember(habit.id) { mutableStateOf(thisWeek) }
    var renaming by remember { mutableStateOf(false) }
    var deleting by remember { mutableStateOf(false) }

    val week = remember(weekStart) { weekDays(weekStart) }
    val done = week.count { it.toEpochDay() in habit.checks }
    val streak = remember(habit, today) { weeksInARow(habit, today) }
    val isThisWeek = weekStart == thisWeek
    val pastWeeks = remember(thisWeek, firstWeek) {
        generateSequence(thisWeek.minusWeeks(1)) { it.minusWeeks(1) }
            .takeWhile { it >= firstWeek }
            .take(26)
            .toList()
    }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            SmallAction("‹ ${stringResource(R.string.back)}", onBack)
            Spacer(Modifier.weight(1f))
            SmallAction(stringResource(R.string.rename)) { renaming = true }
            SmallAction(stringResource(R.string.delete)) { deleting = true }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            text = habit.name,
            style = MaterialTheme.typography.headlineLarge,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = pluralStringResource(R.plurals.times_a_week_n, habit.target, habit.target),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (streak > 0) {
            Spacer(Modifier.height(2.dp))
            Text(
                text = pluralStringResource(R.plurals.weeks_in_a_row, streak, streak),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(Modifier.height(22.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(22.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.target),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )
            Stepper(value = habit.target, onChange = onSetTarget)
        }

        Spacer(Modifier.height(26.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            ArrowText("‹", enabled = weekStart > firstWeek) {
                weekStart = weekStart.minusWeeks(1)
            }
            Column(
                Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
            ) {
                Text(
                    text = weekRangeLabel(weekStart),
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.week_progress, done, habit.target),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            ArrowText("›", enabled = !isThisWeek) {
                weekStart = weekStart.plusWeeks(1)
            }
        }

        Spacer(Modifier.height(14.dp))
        WeekStrip(
            days = week,
            checks = habit.checks,
            today = today,
            onToggle = onToggle,
        )

        if (!isThisWeek) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.back_to_this_week),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { weekStart = thisWeek }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            )
        }

        if (pastWeeks.isNotEmpty()) {
            Spacer(Modifier.height(26.dp))
            SectionLabel(stringResource(R.string.last_weeks))
            Spacer(Modifier.height(2.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 32.dp),
            ) {
                items(pastWeeks) { monday ->
                    PastWeekRow(habit = habit, monday = monday)
                }
            }
        }
    }

    if (renaming) {
        var name by remember { mutableStateOf(habit.name) }
        AlertDialog(
            onDismissRequest = { renaming = false },
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurface,
            title = {
                Text(
                    text = stringResource(R.string.rename),
                    style = MaterialTheme.typography.titleMedium,
                )
            },
            text = {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it.take(48) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = slateFieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRename(name)
                        renaming = false
                    },
                    enabled = name.isNotBlank(),
                ) {
                    Text(
                        text = stringResource(R.string.save),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (name.isNotBlank()) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        },
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { renaming = false }) {
                    Text(
                        text = stringResource(R.string.cancel),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
        )
    }

    if (deleting) {
        AlertDialog(
            onDismissRequest = { deleting = false },
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            title = {
                Text(
                    text = stringResource(R.string.delete_title, habit.name),
                    style = MaterialTheme.typography.titleMedium,
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.delete_body),
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        deleting = false
                        onDelete()
                    },
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { deleting = false }) {
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
private fun PastWeekRow(habit: Habit, monday: LocalDate) {
    val days = remember(monday) { weekDays(monday) }
    val done = weekChecks(habit, monday)
    val met = done >= habit.target

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = weekRangeLabel(monday),
            style = MaterialTheme.typography.bodyMedium,
            color = if (met) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.weight(1f),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            days.forEach { day ->
                val checked = day.toEpochDay() in habit.checks
                Box(
                    Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(
                            if (checked) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.outline
                            },
                        ),
                )
            }
        }
        Spacer(Modifier.width(10.dp))
        Text(
            text = "$done/${habit.target}",
            style = MaterialTheme.typography.labelMedium,
            color = if (met) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            textAlign = TextAlign.End,
            modifier = Modifier.width(28.dp),
        )
    }
}
