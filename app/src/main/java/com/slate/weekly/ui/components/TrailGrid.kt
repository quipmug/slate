package com.slate.weekly.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import kotlin.math.min

@Composable
fun TrailGrid(
    weeks: List<LocalDate>,
    checks: Set<Long>,
    today: LocalDate,
    firstDay: Long,
    modifier: Modifier = Modifier,
    height: Dp = 40.dp,
) {
    val doneColor = MaterialTheme.colorScheme.onSurface
    val restColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
    ) {
        if (weeks.isEmpty()) return@Canvas
        val gap = 2.dp.toPx()
        val columns = weeks.size
        val cell = min(
            (size.width - gap * (columns - 1)) / columns,
            (size.height - gap * 6f) / 7f,
        )
        if (cell <= 0f) return@Canvas

        val radius = CornerRadius(cell * 0.3f)
        val step = cell + gap
        val gridWidth = cell * columns + gap * (columns - 1)
        val gridHeight = cell * 7f + gap * 6f
        val left = (size.width - gridWidth) / 2f
        val top = (size.height - gridHeight) / 2f
        val firstEpoch = weeks.first().toEpochDay()
        val todayEpoch = today.toEpochDay()

        for (column in 0 until columns) {
            for (row in 0..6) {
                val epoch = firstEpoch + column * 7L + row
                if (epoch < firstDay || epoch > todayEpoch) continue
                drawRoundRect(
                    color = if (epoch in checks) doneColor else restColor,
                    topLeft = Offset(left + column * step, top + row * step),
                    size = Size(cell, cell),
                    cornerRadius = radius,
                )
            }
        }
    }
}
