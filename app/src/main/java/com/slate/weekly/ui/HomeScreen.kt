package com.slate.weekly.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.slate.weekly.R
import com.slate.weekly.data.Habit
import com.slate.weekly.util.dayDescription
import com.slate.weekly.util.dayLabel
import com.slate.weekly.util.startOfWeek
import com.slate.weekly.util.weekChecks
import com.slate.weekly.util.weekDays
import com.slate.weekly.util.weekRangeLabel
import java.time.LocalDate
import kotlin.math.min

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
    val visible = remember(active, weekStart) {
        val lastDay = weekStart.plusDays(6).toEpochDay()
        active.filter { it.createdDay <= lastDay }
    }

    Box(Modifier.fillMaxSize()) {
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
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    SmallAction(stringResource(R.string.year), onYear)
                    SmallAction(stringResource(R.string.settings), onSettings)
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                ArrowText(
                    glyph = "‹",
                    description = stringResource(R.string.previous_week),
                    enabled = earliestWeek != null && weekStart > earliestWeek,
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
                    val shown = remember(active, start) {
                        val lastDay = start.plusDays(6).toEpochDay()
                        active.filter { it.createdDay <= lastDay }
                    }
                    val shownDone = shown.sumOf { min(weekChecks(it, start), it.target) }
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
                        } else if (active.isNotEmpty()) {
                            Spacer(Modifier.height(3.dp))
                            Text(
                                text = stringResource(R.string.nothing_this_week),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
                ArrowText(
                    glyph = "›",
                    description = stringResource(R.string.next_week),
                    enabled = earliestWeek != null && !isThisWeek,
                    onClick = { onShiftWeek(1) },
                )
            }

            if (!isThisWeek) {
                Spacer(Modifier.height(2.dp))
                TextAction(
                    text = stringResource(R.string.back_to_this_week),
                    onClick = onThisWeek,
                )
            }

            Spacer(Modifier.height(14.dp))

            if (active.isEmpty()) {
                EmptyState(onAdd)
            } else if (visible.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 112.dp),
                ) {
                    items(visible, key = { it.id }) { habit ->
                        HabitCard(
                            habit = habit,
                            week = week,
                            today = today,
                            onToggle = onToggle,
                            onOpen = onOpen,
                            modifier = Modifier.animateItem(),
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = active.isNotEmpty(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 18.dp),
            enter = fadeIn(tween(160)) + scaleIn(tween(200), initialScale = 0.92f),
            exit = fadeOut(tween(120)) + scaleOut(tween(140), targetScale = 0.92f),
        ) {
            AddPill(
                text = stringResource(R.string.add),
                onClick = onAdd,
                plus = true,
                modifier = Modifier.width(158.dp),
            )
        }
    }
}

@Composable
private fun EmptyState(onAdd: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 64.dp),
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
            plus = true,
            modifier = Modifier.width(172.dp),
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
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(20.dp)
    val done = week.count { it.toEpochDay() in habit.checks }.coerceAtMost(habit.target)
    val met = done >= habit.target

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape)
            .clickable { onOpen(habit.id) }
            .padding(horizontal = 14.dp, vertical = 13.dp),
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
        Spacer(Modifier.height(8.dp))
        WeekStrip(
            days = week,
            checks = habit.checks,
            today = today,
            firstDay = habit.createdDay,
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
    firstDay: Long = Long.MIN_VALUE,
    cell: Dp = 30.dp,
) {
    val haptics = LocalHapticFeedback.current

    Row(Modifier.fillMaxWidth()) {
        days.forEach { day ->
            val epoch = day.toEpochDay()
            val exists = epoch >= firstDay
            val done = epoch in checks
            val enabled = exists && day <= today
            val isToday = day == today

            val labelColor = when {
                !exists -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                isToday -> MaterialTheme.colorScheme.onSurface
                enabled -> MaterialTheme.colorScheme.onSurfaceVariant
                else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
            }
            val borderColor by animateColorAsState(
                targetValue = when {
                    !exists || done -> Color.Transparent
                    isToday -> MaterialTheme.colorScheme.onSurface
                    !enabled -> MaterialTheme.colorScheme.outlineVariant
                    else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                },
                animationSpec = tween(160),
                label = "dayBorder",
            )
            val borderWidth by animateDpAsState(
                targetValue = if (isToday) 1.5.dp else 1.dp,
                animationSpec = tween(160),
                label = "dayBorderWidth",
            )
            val fillColor by animateColorAsState(
                targetValue = if (done) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    Color.Transparent
                },
                animationSpec = tween(160),
                label = "dayFill",
            )
            val fillScale by animateFloatAsState(
                targetValue = if (done) 1f else 0.55f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium,
                ),
                label = "dayScale",
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
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
                    )
                    .semantics { contentDescription = dayDescription(day) },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = dayLabel(day),
                    style = MaterialTheme.typography.labelSmall,
                    color = labelColor,
                    modifier = Modifier.clearAndSetSemantics { },
                )
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier.size(cell),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .border(borderWidth, borderColor, CircleShape),
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = fillScale
                                scaleY = fillScale
                            }
                            .clip(CircleShape)
                            .background(fillColor),
                    )
                }
            }
        }
    }
}
