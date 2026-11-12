package com.slate.weekly.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.slate.weekly.R
import com.slate.weekly.data.Habit
import com.slate.weekly.domain.checkedDaysThisYear
import com.slate.weekly.domain.daysKeptInYear
import com.slate.weekly.domain.daysKeptThisYear
import com.slate.weekly.domain.weeksOfYear
import com.slate.weekly.ui.components.EmptyState
import com.slate.weekly.ui.components.SectionLabel
import com.slate.weekly.ui.components.SlateCard
import com.slate.weekly.ui.components.SlateTopBar
import com.slate.weekly.util.monthName
import com.slate.weekly.util.startOfWeek
import com.slate.weekly.util.weekDays
import java.time.LocalDate
import java.time.Month

@Composable
fun YearScreen(
    habits: List<Habit>,
    today: LocalDate,
    onBack: () -> Unit,
    onAdd: () -> Unit,
) {
    val year = today.year
    val checkedDays = remember(habits, today) { checkedDaysThisYear(habits, today) }
    val weeks = remember(year) { weeksOfYear(year) }
    val kept = remember(habits, today) { daysKeptThisYear(habits, today) }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(4.dp))
        SlateTopBar(onBack = onBack)

        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.your_year),
            style = MaterialTheme.typography.headlineLarge,
        )
        if (habits.isNotEmpty()) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = pluralStringResource(R.plurals.days_kept, kept, kept),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (habits.isEmpty()) {
            EmptyState(
                title = stringResource(R.string.nothing_yet),
                subtitle = stringResource(R.string.nothing_yet_sub),
                action = stringResource(R.string.add),
                onAction = onAdd,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 64.dp),
            )
        } else {
            Spacer(Modifier.height(20.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 32.dp),
            ) {
                item {
                    SlateCard(contentPadding = PaddingValues(14.dp)) {
                        SectionLabel(stringResource(R.string.all_habits))
                        Spacer(Modifier.height(12.dp))
                        YearHeatmap(
                            weeks = weeks,
                            checks = checkedDays,
                            today = today,
                            year = year,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                item {
                    Spacer(Modifier.height(24.dp))
                    SectionLabel(stringResource(R.string.by_habit))
                }

                itemsIndexed(habits, key = { _, habit -> habit.id }) { index, habit ->
                    val count = remember(habit, year, today) {
                        daysKeptInYear(habit, year, today)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .animateItem(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = habit.name,
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = pluralStringResource(R.plurals.days_kept, count, count),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (index < habits.lastIndex) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun YearHeatmap(
    weeks: List<LocalDate>,
    checks: Set<Long>,
    today: LocalDate,
    year: Int,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier) {
        val gap = 2.dp
        val cell = ((maxWidth - gap * (weeks.size - 1).toFloat()) / weeks.size.toFloat())
            .coerceIn(2.dp, 7.dp)
        val gridWidth = cell * weeks.size.toFloat() + gap * (weeks.size - 1).toFloat()
        val step = cell + gap
        val gridStart = weeks.first()
        val monthColumns = remember(gridStart, step) {
            Month.values().map { month ->
                val first = LocalDate.of(gridStart.year, month, 1)
                val column = ((first.toEpochDay() - gridStart.toEpochDay()) / 7).toInt()
                month to (step * column.toFloat())
            }
        }

        Column(
            modifier = Modifier
                .width(gridWidth)
                .align(Alignment.TopCenter),
        ) {
            Box(Modifier.fillMaxWidth().height(14.dp)) {
                monthColumns.forEachIndexed { index, (month, x) ->
                    if (index % 2 == 0) {
                        Text(
                            text = monthName(month, true),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.offset(x = x),
                        )
                    }
                }
            }
            Spacer(Modifier.height(5.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                weeks.forEach { monday ->
                    Column(verticalArrangement = Arrangement.spacedBy(gap)) {
                        weekDays(monday).forEach { day ->
                            val inYear = day.year == year
                            val done = day.toEpochDay() in checks
                            val fill = when {
                                !inYear -> Color.Transparent
                                done -> MaterialTheme.colorScheme.onSurface
                                day > today -> Color.Transparent
                                else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.18f)
                            }
                            Box(
                                Modifier
                                    .size(cell)
                                    .clip(RoundedCornerShape(1.5.dp))
                                    .background(fill),
                            )
                        }
                    }
                }
            }
        }
    }
}
