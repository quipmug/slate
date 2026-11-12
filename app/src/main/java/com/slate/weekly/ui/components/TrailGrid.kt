package com.slate.weekly.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Canvas as GraphicsCanvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.platform.LocalDensity
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

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
    ) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.roundToPx() }.coerceAtLeast(1)
        val heightPx = with(density) { maxHeight.roundToPx() }.coerceAtLeast(1)
        val gapPx = with(density) { 2.dp.toPx() }
        val todayEpoch = remember(today) { today.toEpochDay() }

        // A card can hold 371 tiny cells. Rendering them into a bitmap once is
        // much cheaper than issuing hundreds of rounded rects on every scroll frame.
        val bitmap = remember(
            weeks,
            checks,
            todayEpoch,
            firstDay,
            widthPx,
            heightPx,
            gapPx,
            doneColor,
            restColor,
        ) {
            buildTrailBitmap(
                weeks = weeks,
                checks = checks,
                todayEpoch = todayEpoch,
                firstDay = firstDay,
                widthPx = widthPx,
                heightPx = heightPx,
                gapPx = gapPx,
                doneColor = doneColor,
                restColor = restColor,
            )
        }

        Canvas(Modifier.fillMaxSize()) {
            drawImage(bitmap)
        }
    }
}

private fun buildTrailBitmap(
    weeks: List<LocalDate>,
    checks: Set<Long>,
    todayEpoch: Long,
    firstDay: Long,
    widthPx: Int,
    heightPx: Int,
    gapPx: Float,
    doneColor: Color,
    restColor: Color,
): ImageBitmap {
    val image = ImageBitmap(widthPx, heightPx)
    if (weeks.isEmpty()) return image

    val columns = weeks.size
    val cell = min(
        (widthPx - gapPx * (columns - 1)) / columns,
        (heightPx - gapPx * 6f) / 7f,
    )
    if (cell <= 0f) return image

    val canvas = GraphicsCanvas(image)
    val paint = Paint().apply { isAntiAlias = true }
    val radius = cell * 0.3f
    val step = cell + gapPx
    val gridWidth = cell * columns + gapPx * (columns - 1)
    val gridHeight = cell * 7f + gapPx * 6f
    val left = (widthPx - gridWidth) / 2f
    val top = (heightPx - gridHeight) / 2f
    val firstEpoch = weeks.first().toEpochDay()

    for (column in 0 until columns) {
        for (row in 0..6) {
            val epoch = firstEpoch + column * 7L + row
            if (epoch < firstDay || epoch > todayEpoch) continue
            paint.color = if (epoch in checks) doneColor else restColor
            val x = left + column * step
            val y = top + row * step
            canvas.drawRoundRect(
                left = x,
                top = y,
                right = x + cell,
                bottom = y + cell,
                radiusX = radius,
                radiusY = radius,
                paint = paint,
            )
        }
    }
    return image
}
