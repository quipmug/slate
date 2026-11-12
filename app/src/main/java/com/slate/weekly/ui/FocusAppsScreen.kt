package com.slate.weekly.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.slate.weekly.R
import com.slate.weekly.data.LaunchableApp
import com.slate.weekly.data.loadLaunchableApps
import com.slate.weekly.ui.components.SlateTopBar
import com.slate.weekly.ui.components.slateFieldColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun FocusAppsScreen(
    selected: Set<String>,
    onToggle: (String, Boolean) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    var query by rememberSaveable { mutableStateOf("") }
    val apps by produceState<List<LaunchableApp>?>(initialValue = null) {
        value = withContext(Dispatchers.IO) { loadLaunchableApps(context) }
    }
    val filtered = remember(apps, query) {
        val term = query.trim()
        (apps ?: emptyList()).filter { term.isEmpty() || it.label.contains(term, ignoreCase = true) }
    }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(4.dp))
        SlateTopBar(onBack = onBack) {
            Text(
                text = pluralStringResource(
                    R.plurals.apps_selected,
                    selected.size,
                    selected.size,
                ),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.blocked_apps),
            style = MaterialTheme.typography.headlineLarge,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.focus_apps_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.focus_protected_note),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = {
                Text(
                    text = stringResource(R.string.search),
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            textStyle = MaterialTheme.typography.bodyLarge,
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = slateFieldColors(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(8.dp))
        if (apps == null) {
            Text(
                text = stringResource(R.string.loading),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 12.dp),
            )
        } else if (filtered.isEmpty()) {
            Text(
                text = stringResource(R.string.nothing_found),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 12.dp),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 32.dp),
            ) {
                items(
                    items = filtered,
                    key = { it.packageName },
                    contentType = { "app" },
                ) { app ->
                    AppRow(
                        app = app,
                        selected = app.packageName in selected,
                        onToggle = onToggle,
                    )
                }
            }
        }
    }
}

@Composable
private fun AppRow(
    app: LaunchableApp,
    selected: Boolean,
    onToggle: (String, Boolean) -> Unit,
) {
    val rowShape = RoundedCornerShape(12.dp)
    val checkShape = RoundedCornerShape(6.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .clip(rowShape)
            .toggleable(
                value = selected,
                role = Role.Checkbox,
                onValueChange = { onToggle(app.packageName, it) },
            )
            .padding(horizontal = 4.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = app.label,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(checkShape)
                .then(
                    if (selected) {
                        Modifier.background(MaterialTheme.colorScheme.onSurface)
                    } else {
                        Modifier.border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = checkShape,
                        )
                    },
                ),
        )
    }
}
