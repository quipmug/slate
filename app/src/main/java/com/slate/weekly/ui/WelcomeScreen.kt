package com.slate.weekly.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.slate.weekly.R
import com.slate.weekly.ui.components.AddPill
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    onBegin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fill = remember { Animatable(0f) }
    var titleIn by remember { mutableStateOf(false) }
    var actionsIn by remember { mutableStateOf(false) }
    val titleReveal by animateFloatAsState(
        targetValue = if (titleIn) 1f else 0f,
        animationSpec = tween(720, easing = FastOutSlowInEasing),
        label = "welcomeTitle",
    )
    val actionReveal by animateFloatAsState(
        targetValue = if (actionsIn) 1f else 0f,
        animationSpec = tween(520, easing = FastOutSlowInEasing),
        label = "welcomeAction",
    )

    LaunchedEffect(Unit) {
        fill.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1150,
                delayMillis = 160,
                easing = FastOutSlowInEasing,
            ),
        )
    }
    LaunchedEffect(Unit) {
        delay(260)
        titleIn = true
    }
    LaunchedEffect(Unit) {
        delay(920)
        actionsIn = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent(PointerEventPass.Final)
                            .changes
                            .forEach { it.consume() }
                    }
                }
            }
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))

        WelcomeTrail(progress = { fill.value })

        Spacer(Modifier.height(34.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .graphicsLayer {
                    alpha = titleReveal
                    translationY = (1f - titleReveal) * 14.dp.toPx()
                },
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displayLarge,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.welcome_tagline),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(Modifier.weight(1f))

        Box(
            modifier = Modifier
                .width(172.dp)
                .height(54.dp)
                .graphicsLayer {
                    alpha = actionReveal
                    translationY = (1f - actionReveal) * 10.dp.toPx()
                },
            contentAlignment = Alignment.Center,
        ) {
            AddPill(
                text = stringResource(R.string.begin),
                onClick = onBegin,
                enabled = actionsIn,
                modifier = Modifier
                    .width(172.dp)
                    .height(54.dp),
            )
        }
        Spacer(Modifier.height(30.dp))
    }
}

@Composable
private fun WelcomeTrail(progress: () -> Float) {
    val doneColor = MaterialTheme.colorScheme.onSurface
    val restColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.16f)

    Canvas(
        modifier = Modifier
            .width(162.dp)
            .height(86.dp),
    ) {
        val columns = 9
        val rows = 5
        val gap = 5.dp.toPx()
        val cell = minOf(
            (size.width - gap * (columns - 1)) / columns,
            (size.height - gap * (rows - 1)) / rows,
        )
        if (cell <= 0f) return@Canvas

        val step = cell + gap
        val gridWidth = cell * columns + gap * (columns - 1)
        val gridHeight = cell * rows + gap * (rows - 1)
        val left = (size.width - gridWidth) / 2f
        val top = (size.height - gridHeight) / 2f
        val radius = CornerRadius(cell * 0.3f)
        val total = (columns * rows).toFloat()
        val reveal = progress()

        for (column in 0 until columns) {
            for (row in 0 until rows) {
                val index = column * rows + row
                val cellReveal = ((reveal * (total + 10f) - index) / 3f)
                    .coerceIn(0f, 1f)
                val origin = Offset(
                    x = left + column * step,
                    y = top + row * step,
                )
                drawRoundRect(
                    color = restColor,
                    topLeft = origin,
                    size = Size(cell, cell),
                    cornerRadius = radius,
                )
                if (cellReveal > 0f) {
                    drawRoundRect(
                        color = doneColor.copy(alpha = cellReveal),
                        topLeft = origin,
                        size = Size(cell, cell),
                        cornerRadius = radius,
                    )
                }
            }
        }
    }
}
