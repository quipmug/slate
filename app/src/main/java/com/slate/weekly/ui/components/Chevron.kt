package com.slate.weekly.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class ChevronDirection { Left, Right }

@Composable
fun Chevron(
    direction: ChevronDirection,
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current,
    strokeWidth: Dp = 1.6.dp,
) {
    Canvas(modifier) {
        val path = Path().apply {
            if (direction == ChevronDirection.Left) {
                moveTo(size.width * 0.64f, size.height * 0.22f)
                lineTo(size.width * 0.36f, size.height * 0.5f)
                lineTo(size.width * 0.64f, size.height * 0.78f)
            } else {
                moveTo(size.width * 0.36f, size.height * 0.22f)
                lineTo(size.width * 0.64f, size.height * 0.5f)
                lineTo(size.width * 0.36f, size.height * 0.78f)
            }
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = strokeWidth.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
            ),
        )
    }
}

@Composable
fun ChevronButton(
    direction: ChevronDirection,
    description: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current,
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(enabled = enabled, onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Chevron(
            direction = direction,
            modifier = Modifier.size(20.dp),
            color = color,
        )
    }
}
