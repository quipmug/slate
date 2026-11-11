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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.slate.weekly.R
import com.slate.weekly.data.Habit
import com.slate.weekly.util.daysKeptThisYear
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
    val active = remember(habits) { habits.filter { !it.archived } }
    val year = today.year
    val checkedDays = remember(active) { active.flatMap { it.checks }.toSet() }
    val gridStart = remember(year) { LocalDate.of(year, 1, 1).startOfWeek() }
    val columns = remember(gridStart, year) {
        val last = LocalDate.of(year, 12, 31)
        val count = ((last.toEpochDay() - gridStart.toEpochDay()) / 7).toInt() + 1
        List(count) { gridStart.plusWeeks(it.toLong()) }
    }
    val kept = remember(active, today) { daysKeptThisYear(active, today) }

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
            text = stringResource(R.string.your_year),
            style = MaterialTheme.typography.headlineLarge,
        )
        if (active.isNotEmpty()) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = pluralStringResource(R.plurals.days_kept, kept, kept),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (active.isEmpty()) {
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
        } else {
            Spacer(Modifier.height(24.dp))
            SectionLabel(stringResource(R.string.all_routines))
            Spacer(Modifier.height(10.dp))
            YearHeatmap(
                columns = columns,
                checks = checkedDays,
                today = today,
                year = year,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(24.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 32.dp),
            ) {
                itemsIndexed(active, key = { _, habit -> habit.id }) { index, habit ->
                    val count = remember(habit, year, today) {
                        habit.checks.count { epochDay ->
                            val date = LocalDate.ofEpochDay(epochDay)
                            date.year == year && date <= today
                        }
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
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = pluralStringResource(R.plurals.days_kept, count, count),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (index < active.lastIndex) {
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
    columns: List<LocalDate>,
    checks: Set<Long>,
    today: LocalDate,
    year: Int,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier) {
        val gap = 2.dp
        val cell = ((maxWidth - gap * (columns.size - 1).toFloat()) / columns.size.toFloat())
            .coerceIn(2.dp, 7.dp)
        val gridWidth = cell * columns.size.toFloat() + gap * (columns.size - 1).toFloat()

        Column(Modifier.width(gridWidth)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = monthName(Month.JANUARY, true),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                )
                Text(
                    text = monthName(Month.DECEMBER, true),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                )
            }
            Spacer(Modifier.height(5.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                columns.forEach { monday ->
                    Column(verticalArrangement = Arrangement.spacedBy(gap)) {
                        weekDays(monday).forEach { day ->
                            val inYear = day.year == year
                            val done = day.toEpochDay() in checks
                            val fill = when {
                                !inYear -> MaterialTheme.colorScheme.background
                                done -> MaterialTheme.colorScheme.onSurface
                                day > today -> MaterialTheme.colorScheme.background
                                else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
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
