package com.slate.weekly.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.slate.weekly.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitSheet(
    onDismiss: () -> Unit,
    onAdd: (String, Int) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var target by remember { mutableIntStateOf(3) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onSurface,
        scrimColor = Color.Black.copy(alpha = 0.35f),
        dragHandle = {
            Box(
                Modifier
                    .padding(top = 12.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outline),
            )
        },
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 30.dp),
        ) {
            Text(
                text = stringResource(R.string.new_habit),
                style = MaterialTheme.typography.headlineLarge,
            )
            Spacer(Modifier.height(20.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it.take(48) },
                placeholder = {
                    Text(
                        text = stringResource(R.string.name_hint),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                },
                textStyle = MaterialTheme.typography.titleMedium,
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                colors = slateFieldColors(),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(22.dp))
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.times_a_week),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(Modifier.weight(1f))
                Stepper(value = target, onChange = { target = it })
            }
            Spacer(Modifier.height(26.dp))
            AddPill(
                text = stringResource(R.string.add),
                onClick = { if (name.isNotBlank()) onAdd(name, target) },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank(),
            )
        }
    }
}
