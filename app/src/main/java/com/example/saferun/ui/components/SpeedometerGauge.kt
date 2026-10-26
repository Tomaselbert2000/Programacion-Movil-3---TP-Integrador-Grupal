package com.example.saferun.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

private const val START_ANGLE = 135f
private const val SWEEP_ANGLE = 270f
private const val TICK_COUNT = 12

/**
 * Canvas speedometer: a 270° arc that shifts from green to amber to red as the
 * rider approaches the gauge limit. The numeric value is rendered as Compose
 * text so it keeps the app typography and accessibility scaling.
 */
@Composable
fun SpeedometerGauge(
    speedKmh: Float,
    modifier: Modifier = Modifier,
    maxSpeedKmh: Float = 60f,
    unit: String = "km/h",
    maxSpeedLabel: String? = null,
) {
    val animated by animateFloatAsState(
        targetValue = speedKmh.coerceIn(0f, maxSpeedKmh),
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "speedometer",
    )
    val ratio = (animated / maxSpeedKmh).coerceIn(0f, 1f)

    val scheme = MaterialTheme.colorScheme
    val trackColor = scheme.outlineVariant
    val labelColor = scheme.onSurfaceVariant
    val accent = remember(ratio, scheme) { gaugeColor(ratio, scheme.primary, scheme.tertiary, scheme.error) }
    val textMeasurer = rememberTextMeasurer()
    val tickStyle = remember(scheme) {
        TextStyle(
            color = scheme.onSurfaceVariant,
            fontSize = 9.sp,
            letterSpacing = 0.sp,
        )
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val minSide = size.minDimension
            val strokeWidth = minSide * 0.075f
            val outerInset = minSide * 0.11f
            val radius = minSide / 2 - outerInset
            val center = Offset(size.width / 2, size.height / 2)
            val arcSize = Size(radius * 2, radius * 2)
            val arcTopLeft = Offset(center.x - radius, center.y - radius)

            // Soft glow behind the active portion.
            if (ratio > 0.01f) {
                drawArc(
                    color = accent.copy(alpha = 0.18f),
                    startAngle = START_ANGLE,
                    sweepAngle = SWEEP_ANGLE * ratio,
                    useCenter = false,
                    topLeft = arcTopLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth * 2.1f, cap = StrokeCap.Round),
                )
            }

            drawArc(
                color = trackColor,
                startAngle = START_ANGLE,
                sweepAngle = SWEEP_ANGLE,
                useCenter = false,
                topLeft = arcTopLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )

            if (ratio > 0.005f) {
                drawArc(
                    color = accent,
                    startAngle = START_ANGLE,
                    sweepAngle = SWEEP_ANGLE * ratio,
                    useCenter = false,
                    topLeft = arcTopLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                )
            }

            // Ticks and scale numbers.
            for (index in 0..TICK_COUNT) {
                val angleDeg = START_ANGLE + SWEEP_ANGLE * index / TICK_COUNT
                val angleRad = Math.toRadians(angleDeg.toDouble())
                val dx = cos(angleRad).toFloat()
                val dy = sin(angleRad).toFloat()
                val isMajor = index % 3 == 0

                val innerRadius = radius - strokeWidth * 0.5f - minSide * 0.018f
                val tickLength = minSide * (if (isMajor) 0.045f else 0.025f)
                drawLine(
                    color = if (isMajor) labelColor else trackColor,
                    start = Offset(center.x + dx * innerRadius, center.y + dy * innerRadius),
                    end = Offset(
                        center.x + dx * (innerRadius - tickLength),
                        center.y + dy * (innerRadius - tickLength),
                    ),
                    strokeWidth = if (isMajor) 2.2f else 1.4f,
                    cap = StrokeCap.Round,
                )

                if (isMajor) {
                    val value = (maxSpeedKmh * index / TICK_COUNT).toInt()
                    val text = textMeasurer.measure(value.toString(), tickStyle)
                    val labelRadius = radius + outerInset * 0.42f
                    drawText(
                        textLayoutResult = text,
                        topLeft = Offset(
                            center.x + dx * labelRadius - text.size.width / 2f,
                            center.y + dy * labelRadius - text.size.height / 2f,
                        ),
                    )
                }
            }

            // Active end cap.
            if (ratio > 0.005f) {
                val endRad = Math.toRadians((START_ANGLE + SWEEP_ANGLE * ratio).toDouble())
                drawCircle(
                    color = accent,
                    radius = strokeWidth * 0.38f,
                    center = Offset(
                        center.x + cos(endRad).toFloat() * radius,
                        center.y + sin(endRad).toFloat() * radius,
                    ),
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 4.dp),
        ) {
            Text(
                text = String.format(Locale.getDefault(), "%.1f", animated),
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 58.sp),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (maxSpeedLabel != null) {
                Text(
                    text = maxSpeedLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 3.dp),
                )
            }
        }
    }
}

private fun gaugeColor(ratio: Float, primary: Color, tertiary: Color, error: Color): Color =
    when {
        ratio <= 0.5f -> lerp(primary, tertiary, ratio / 0.5f)
        else -> lerp(tertiary, error, ((ratio - 0.5f) / 0.5f).coerceIn(0f, 1f))
    }
