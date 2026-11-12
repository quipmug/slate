package com.slate.weekly.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.slate.weekly.R
import com.slate.weekly.data.Habit
import com.slate.weekly.domain.doneInWeek
import com.slate.weekly.ui.components.ChevronButton
import com.slate.weekly.ui.components.ChevronDirection
import com.slate.weekly.ui.components.EmptyState
import com.slate.weekly.ui.components.PlusAction
import com.slate.weekly.ui.components.SlateCard
import com.slate.weekly.ui.components.TextAction
import com.slate.weekly.ui.components.TrailGrid
import com.slate.weekly.ui.components.WeekStrip
import com.slate.weekly.util.startOfWeek
import com.slate.weekly.util.weekDays
import com.slate.weekly.util.weekRangeLabel
import com.slate.weekly.util.weeksEndingAt
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
    onAdd: () -> Unit,
) {
    val week = remember(weekStart) { weekDays(weekStart) }
    val trail = remember(today) { weeksEndingAt(today.startOfWeek(), 53) }
    val isThisWeek = weekStart == today.startOfWeek()
    val earliestWeek = remember(habits) {
        habits.minOfOrNull { LocalDate.ofEpochDay(it.createdDay).startOfWeek() }
    }
    val visible = remember(habits, weekStart) {
        val lastDay = weekStart.plusDays(6).toEpochDay()
        habits.filter { it.createdDay <= lastDay }
    }
    val canGoBack = earliestWeek != null && weekStart > earliestWeek
    val canGoForward = earliestWeek != null && !isThisWeek

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(4.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleMedium,
            )
            PlusAction(
                onClick = onAdd,
                modifier = Modifier.offset(x = 6.dp),
            )
        }

        Spacer(Modifier.height(2.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            ChevronButton(
                direction = ChevronDirection.Left,
                description = stringResource(R.string.previous_week),
                enabled = canGoBack,
                color = if (canGoBack) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f)
                },
                onClick = { onShiftWeek(-1) },
            )
            AnimatedContent(
                targetState = weekStart,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                transitionSpec = {
                    val forward = targetState > initialState
                    (fadeIn(tween(180)) + slideInHorizontally(tween(260)) {
                        if (forward) it / 5 else -it / 5
                    }) togetherWith
                        (fadeOut(tween(120)) + slideOutHorizontally(tween(180)) {
                            if (forward) -it / 5 else it / 5
                        })
                },
                label = "weekHeader",
            ) { start ->
                val shown = remember(habits, start) {
                    val lastDay = start.plusDays(6).toEpochDay()
                    habits.filter { it.createdDay <= lastDay }
                }
                val shownDone = shown.sumOf { doneInWeek(it, start) }
                val shownTarget = shown.sumOf { it.target }

                Column {
                    Text(
                        text = weekRangeLabel(start),
                        style = MaterialTheme.typography.headlineLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (shown.isNotEmpty()) {
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = if (shownDone >= shownTarget) {
                                stringResource(R.string.week_complete)
                            } else {
                                stringResource(R.string.week_progress, shownDone, shownTarget)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else if (habits.isNotEmpty()) {
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = stringResource(R.string.nothing_this_week),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            ChevronButton(
                direction = ChevronDirection.Right,
                description = stringResource(R.string.next_week),
                enabled = canGoForward,
                color = if (canGoForward) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f)
                },
                onClick = { onShiftWeek(1) },
            )
        }

        AnimatedVisibility(
            visible = !isThisWeek,
            enter = fadeIn(tween(160)) + expandVertically(tween(220)),
            exit = fadeOut(tween(100)) + shrinkVertically(tween(160)),
        ) {
            Column {
                Spacer(Modifier.height(2.dp))
                TextAction(
                    text = stringResource(R.string.back_to_this_week),
                    onClick = onThisWeek,
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        if (habits.isEmpty()) {
            EmptyState(
                title = stringResource(R.string.nothing_yet),
                subtitle = stringResource(R.string.nothing_yet_sub),
                action = stringResource(R.string.add),
                onAction = onAdd,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 96.dp),
            )
        } else if (visible.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 132.dp),
            ) {
                items(
                    items = visible,
                    key = { it.id },
                    contentType = { "habit" },
                ) { habit ->
                    HabitCard(
                        habit = habit,
                        week = week,
                        trail = trail,
                        today = today,
                        onToggle = onToggle,
                        onOpen = onOpen,
                        modifier = Modifier.animateItem(),
                    )
                }
            }
        }
    }
}

@Composable
private fun HabitCard(
    habit: Habit,
    week: List<LocalDate>,
    trail: List<LocalDate>,
    today: LocalDate,
    onToggle: (String, LocalDate) -> Unit,
    onOpen: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val done = doneInWeek(habit, week.first())
    val met = done >= habit.target

    SlateCard(
        modifier = modifier,
        onClick = { onOpen(habit.id) },
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 13.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = habit.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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
        Spacer(Modifier.height(8.dp))
        WeekStrip(
            days = week,
            checks = habit.checks,
            today = today,
            firstDay = habit.createdDay,
            onToggle = { onToggle(habit.id, it) },
        )
        Spacer(Modifier.height(10.dp))
        TrailGrid(
            weeks = trail,
            checks = habit.checks,
            today = today,
            firstDay = habit.createdDay,
        )
    }
}
