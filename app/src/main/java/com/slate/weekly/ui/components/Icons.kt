package com.slate.weekly.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

enum class DockGlyph { Habits, Year, Settings }

@Composable
fun PlusIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val stroke = 1.7.dp.toPx()
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val half = size.minDimension * 0.34f
        drawLine(
            color = color,
            start = Offset(centerX - half, centerY),
            end = Offset(centerX + half, centerY),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(centerX, centerY - half),
            end = Offset(centerX, centerY + half),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
fun DockGlyph(kind: DockGlyph, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val unit = size.minDimension
        when (kind) {
            DockGlyph.Habits -> {
                val cell = unit * 0.34f
                val gap = unit * 0.14f
                val origin = (unit - (cell * 2f + gap)) / 2f
                val radius = CornerRadius(cell * 0.3f)
                for (row in 0..1) {
                    for (column in 0..1) {
                        drawRoundRect(
                            color = color,
                            topLeft = Offset(
                                origin + column * (cell + gap),
                                origin + row * (cell + gap),
                            ),
                            size = Size(cell, cell),
                            cornerRadius = radius,
                        )
                    }
                }
            }

            DockGlyph.Year -> {
                val cell = unit * 0.2f
                val gap = unit * 0.1f
                val origin = (unit - (cell * 3f + gap * 2f)) / 2f
                val radius = CornerRadius(cell * 0.32f)
                for (row in 0..2) {
                    for (column in 0..2) {
                        drawRoundRect(
                            color = color,
                            topLeft = Offset(
                                origin + column * (cell + gap),
                                origin + row * (cell + gap),
                            ),
                            size = Size(cell, cell),
                            cornerRadius = radius,
                        )
                    }
                }
            }

            DockGlyph.Settings -> {
                val stroke = unit * 0.09f
                val knob = unit * 0.1f
                val left = unit * 0.1f
                val right = unit * 0.9f
                val rows = listOf(0.26f, 0.5f, 0.74f)
                val knobs = listOf(0.66f, 0.36f, 0.6f)
                rows.forEachIndexed { index, y ->
                    val lineY = unit * y
                    drawLine(
                        color = color,
                        start = Offset(left, lineY),
                        end = Offset(right, lineY),
                        strokeWidth = stroke,
                        cap = StrokeCap.Round,
                    )
                    drawCircle(
                        color = color,
                        radius = knob,
                        center = Offset(unit * knobs[index], lineY),
                    )
                }
            }
        }
    }
}
