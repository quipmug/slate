package com.slate.weekly.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SmallAction(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 9.dp, vertical = 7.dp),
    )
}

@Composable
fun ArrowText(glyph: String, enabled: Boolean, onClick: () -> Unit) {
    Text(
        text = glyph,
        fontSize = 26.sp,
        color = if (enabled) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
        },
        modifier = Modifier
            .clip(CircleShape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 2.dp),
    )
}

@Composable
fun AddPill(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Box(
        modifier = modifier
            .height(54.dp)
            .clip(CircleShape)
            .background(if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = if (enabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun TextAction(
    text: String,
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.onSurface,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = color,
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 9.dp),
    )
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
fun slateFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.onSurface,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    cursorColor = MaterialTheme.colorScheme.onSurface,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
)

@Composable
fun Stepper(
    value: Int,
    onChange: (Int) -> Unit,
    min: Int = 1,
    max: Int = 7,
    step: Int = 1,
    pad: Boolean = false,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        StepButton("−", enabled = value > min) { onChange((value - step).coerceAtLeast(min)) }
        Text(
            text = if (pad) value.toString().padStart(2, '0') else value.toString(),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(30.dp),
        )
        StepButton("+", enabled = value < max) { onChange((value + step).coerceAtMost(max)) }
    }
}

@Composable
private fun StepButton(glyph: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = glyph,
            fontSize = 20.sp,
            color = if (enabled) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
            },
        )
    }
}

@Composable
fun Toggle(checked: Boolean, onChange: (Boolean) -> Unit) {
    val knob by animateDpAsState(if (checked) 22.dp else 3.dp, label = "knob")
    Box(
        modifier = Modifier
            .width(48.dp)
            .height(28.dp)
            .clip(CircleShape)
            .background(
                if (checked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline,
            )
            .clickable { onChange(!checked) },
    ) {
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(x = knob.roundToPx(), y = 3.dp.roundToPx())
                }
                .size(22.dp)
                .clip(CircleShape)
                .background(
                    if (checked) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.surface,
                ),
        )
    }
}

@Composable
fun Divider() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outline),
    )
}

@Composable
fun EmptyGap() {
    Spacer(Modifier.height(0.dp))
}
