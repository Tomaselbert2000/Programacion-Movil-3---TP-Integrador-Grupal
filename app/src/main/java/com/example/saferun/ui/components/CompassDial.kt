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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

private val CARDINALS = listOf("N", "E", "S", "O")

/**
 * Compass rose with a needle that follows [headingDegrees] (0° = north).
 * Cardinal letters stay fixed so the needle can be read at a glance.
 */
@Composable
fun CompassDial(
    headingDegrees: Float,
    modifier: Modifier = Modifier,
) {
    val animated by animateFloatAsState(
        targetValue = ((headingDegrees % 360f) + 360f) % 360f,
        animationSpec = tween(durationMillis = 500),
        label = "compass",
    )

    val scheme = MaterialTheme.colorScheme
    val needle = scheme.primary
    val tail = scheme.outline
    val ring = scheme.outlineVariant
    val fill = scheme.surfaceVariant.copy(alpha = 0.45f)
    val textMeasurer = rememberTextMeasurer()
    val cardinalStyle = remember(scheme) {
        TextStyle(
            color = scheme.onSurfaceVariant,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
    val northStyle = remember(scheme) {
        cardinalStyle.copy(color = scheme.primary, fontWeight = FontWeight.Bold)
    }

    Canvas(modifier = modifier) {
        val minSide = size.minDimension
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = minSide / 2f - 2.dp.toPx()

        drawCircle(color = fill, radius = radius, center = center)
        drawCircle(
            color = ring,
            radius = radius,
            center = center,
            style = Stroke(width = 1.dp.toPx()),
        )

        // Ticks: every 15°, longer and brighter every 45°.
        for (index in 0 until 24) {
            val angleRad = Math.toRadians((index * 15f - 90f).toDouble())
            val isMajor = index % 3 == 0
            val outer = radius - 3.dp.toPx()
            val length = if (isMajor) 7.dp.toPx() else 4.dp.toPx()
            val dx = cos(angleRad).toFloat()
            val dy = sin(angleRad).toFloat()
            drawLine(
                color = if (isMajor) scheme.onSurfaceVariant else ring,
                start = Offset(center.x + dx * outer, center.y + dy * outer),
                end = Offset(center.x + dx * (outer - length), center.y + dy * (outer - length)),
                strokeWidth = if (isMajor) 1.6.dp.toPx() else 1.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }

        // Cardinal letters.
        CARDINALS.forEachIndexed { index, label ->
            val angleRad = Math.toRadians((index * 90f - 90f).toDouble())
            val style = if (index == 0) northStyle else cardinalStyle
            val text = textMeasurer.measure(label, style)
            val letterRadius = radius * 0.68f
            drawText(
                textLayoutResult = text,
                topLeft = Offset(
                    center.x + cos(angleRad).toFloat() * letterRadius - text.size.width / 2f,
                    center.y + sin(angleRad).toFloat() * letterRadius - text.size.height / 2f,
                ),
            )
        }

        // Needle: north half in the brand colour, south half muted.
        val tip = radius * 0.56f
        val tailLength = radius * 0.34f
        val halfWidth = radius * 0.075f

        rotate(degrees = animated, pivot = center) {
            val back = Path().apply {
                moveTo(center.x, center.y)
                lineTo(center.x + halfWidth, center.y)
                lineTo(center.x, center.y + tailLength)
                lineTo(center.x - halfWidth, center.y)
                close()
            }
            drawPath(path = back, color = tail)

            val front = Path().apply {
                moveTo(center.x, center.y - tip)
                lineTo(center.x + halfWidth, center.y)
                lineTo(center.x, center.y)
                lineTo(center.x - halfWidth, center.y)
                close()
            }
            drawPath(path = front, color = needle)
        }

        drawCircle(color = scheme.surface, radius = 4.5.dp.toPx(), center = center)
        drawCircle(color = needle, radius = 2.6.dp.toPx(), center = center)
    }
}

/** Spanish cardinal name for a heading in degrees. */
fun cardinalName(headingDegrees: Float): String {
    val normalized = ((headingDegrees % 360f) + 360f) % 360f
    return when {
        normalized < 22.5f || normalized >= 337.5f -> "N"
        normalized < 67.5f -> "NE"
        normalized < 112.5f -> "E"
        normalized < 157.5f -> "SE"
        normalized < 202.5f -> "S"
        normalized < 247.5f -> "SO"
        normalized < 292.5f -> "O"
        else -> "NO"
    }
}
