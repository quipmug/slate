package com.slate.weekly.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slate.weekly.R

@Composable
fun slateFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.onSurface,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    cursorColor = MaterialTheme.colorScheme.onSurface,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
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
        StepButton(
            glyph = "−",
            description = stringResource(R.string.decrease),
            enabled = value > min,
        ) {
            onChange((value - step).coerceAtLeast(min))
        }
        Text(
            text = if (pad) value.toString().padStart(2, '0') else value.toString(),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(34.dp),
        )
        StepButton(
            glyph = "+",
            description = stringResource(R.string.increase),
            enabled = value < max,
        ) {
            onChange((value + step).coerceAtMost(max))
        }
    }
}

@Composable
private fun StepButton(
    glyph: String,
    description: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(enabled = enabled, onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = glyph,
            fontSize = 20.sp,
            color = if (enabled) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            },
            modifier = Modifier.clearAndSetSemantics { },
        )
    }
}

@Composable
fun Toggle(checked: Boolean, onChange: (Boolean) -> Unit) {
    val knob by animateDpAsState(
        targetValue = if (checked) 23.dp else 3.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "knob",
    )
    val track by animateColorAsState(
        targetValue = if (checked) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.outline
        },
        animationSpec = tween(180),
        label = "track",
    )
    val knobColor by animateColorAsState(
        targetValue = if (checked) {
            MaterialTheme.colorScheme.background
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(180),
        label = "knobColor",
    )

    Box(
        modifier = Modifier
            .size(width = 48.dp, height = 48.dp)
            .toggleable(
                value = checked,
                role = Role.Switch,
                onValueChange = onChange,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(width = 48.dp, height = 28.dp)
                .clip(CircleShape)
                .background(track),
        ) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(x = knob.roundToPx(), y = 3.dp.roundToPx()) }
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(knobColor),
            )
        }
    }
}
