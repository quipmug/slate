package com.slate.weekly.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.slate.weekly.util.dayDescription
import com.slate.weekly.util.dayLabel
import java.time.LocalDate

@Composable
fun WeekStrip(
    days: List<LocalDate>,
    checks: Set<Long>,
    today: LocalDate,
    onToggle: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    firstDay: Long = Long.MIN_VALUE,
    cell: Dp = 30.dp,
) {
    val haptics = LocalHapticFeedback.current

    Row(modifier.fillMaxWidth()) {
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
                    .heightIn(min = 56.dp)
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
