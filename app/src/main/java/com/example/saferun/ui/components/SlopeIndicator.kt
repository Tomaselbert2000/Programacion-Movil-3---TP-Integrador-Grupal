package com.example.saferun.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

private const val MAX_SLOPE = 15f

/**
 * Bubble level style inclinometer for the current slope. Positive values are
 * climbs (amber), negative values are descents (teal), near zero stays green.
 */
@Composable
fun SlopeIndicator(
    degrees: Float,
    modifier: Modifier = Modifier,
) {
    val clamped = degrees.coerceIn(-MAX_SLOPE, MAX_SLOPE)
    val animated by animateFloatAsState(
        targetValue = clamped,
        animationSpec = tween(durationMillis = 500),
        label = "slope",
    )

    val scheme = MaterialTheme.colorScheme
    val bubbleColor = when {
        animated > 1.5f -> scheme.tertiary
        animated < -1.5f -> scheme.secondary
        else -> scheme.primary
    }
    val trackColor = remember(scheme) { scheme.outlineVariant }
    val tickColor = remember(scheme) { scheme.outline }

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val half = size.width / 2f - 10.dp.toPx()

        drawLine(
            color = trackColor,
            start = Offset(center.x - half, center.y),
            end = Offset(center.x + half, center.y),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round,
        )

        // Ticks at -10, -5, 0, 5 and 10 degrees.
        listOf(-10f, -5f, 0f, 5f, 10f).forEach { tick ->
            val x = center.x + (tick / MAX_SLOPE) * half
            val isCenter = tick == 0f
            drawLine(
                color = if (isCenter) scheme.onSurfaceVariant else tickColor,
                start = Offset(x, center.y - if (isCenter) 8.dp.toPx() else 5.dp.toPx()),
                end = Offset(x, center.y + if (isCenter) 8.dp.toPx() else 5.dp.toPx()),
                strokeWidth = if (isCenter) 1.5.dp.toPx() else 1.dp.toPx(),
            )
        }

        val bubbleX = center.x + (animated / MAX_SLOPE) * half
        drawCircle(
            color = bubbleColor.copy(alpha = 0.18f),
            radius = 8.dp.toPx(),
            center = Offset(bubbleX, center.y),
        )
        drawCircle(
            color = bubbleColor,
            radius = 5.dp.toPx(),
            center = Offset(bubbleX, center.y),
        )
    }
}

/** Colour that matches [SlopeIndicator] for the numeric value. */
@Composable
fun slopeColor(degrees: Float): Color = when {
    degrees > 1.5f -> MaterialTheme.colorScheme.tertiary
    degrees < -1.5f -> MaterialTheme.colorScheme.secondary
    else -> MaterialTheme.colorScheme.onSurface
}
