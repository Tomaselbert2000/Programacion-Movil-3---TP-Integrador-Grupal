package com.example.saferun.ui.scanner

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.saferun.R
import com.example.saferun.ui.components.SafeRunCard
import com.example.saferun.ui.components.ScreenHeader
import com.example.saferun.ui.components.StatusChip
import com.example.saferun.ui.components.StatusTone

@Composable
fun ScannerScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(10.dp))
        ScreenHeader(
            title = stringResource(R.string.scanner_title),
            subtitle = stringResource(R.string.scanner_subtitle),
            trailing = {
                StatusChip(
                    text = stringResource(R.string.scanner_badge),
                    tone = StatusTone.Warning,
                )
            },
        )
        Spacer(Modifier.height(18.dp))
        ScannerPreview()
        Spacer(Modifier.height(16.dp))
        SafeRunCard {
            Text(
                text = stringResource(R.string.scanner_placeholder_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.scanner_placeholder_body),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(12.dp))
        SafeRunCard {
            FeatureRow(stringResource(R.string.scanner_feature_ocr))
            Spacer(Modifier.height(10.dp))
            FeatureRow(stringResource(R.string.scanner_feature_translate))
            Spacer(Modifier.height(10.dp))
            FeatureRow(stringResource(R.string.scanner_feature_offline))
        }
        Spacer(Modifier.height(18.dp))
        Button(
            onClick = {},
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        ) {
            Text(
                text = stringResource(R.string.scanner_action),
                style = MaterialTheme.typography.labelLarge,
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.scanner_disabled),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ScannerPreview() {
    val transition = rememberInfiniteTransition(label = "scanner")
    val scanProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "scanline",
    )
    val accent = MaterialTheme.colorScheme.primary
    val backdrop = Color(0xFF0B1220)
    val shape = RoundedCornerShape(28.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(shape)
            .background(backdrop)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val inset = 26.dp.toPx()
            val bracket = 30.dp.toPx()
            val stroke = 3.dp.toPx()
            val cornerRadius = CornerRadius(10.dp.toPx())

            // Frame corners.
            drawRoundRect(
                color = accent,
                topLeft = Offset(inset, inset),
                size = Size(bracket, bracket),
                cornerRadius = cornerRadius,
                style = Stroke(width = stroke),
            )
            drawRoundRect(
                color = accent,
                topLeft = Offset(size.width - inset - bracket, inset),
                size = Size(bracket, bracket),
                cornerRadius = cornerRadius,
                style = Stroke(width = stroke),
            )
            drawRoundRect(
                color = accent,
                topLeft = Offset(inset, size.height - inset - bracket),
                size = Size(bracket, bracket),
                cornerRadius = cornerRadius,
                style = Stroke(width = stroke),
            )
            drawRoundRect(
                color = accent,
                topLeft = Offset(size.width - inset - bracket, size.height - inset - bracket),
                size = Size(bracket, bracket),
                cornerRadius = cornerRadius,
                style = Stroke(width = stroke),
            )

            // Camera glyph.
            val cameraWidth = 52.dp.toPx()
            val cameraHeight = 38.dp.toPx()
            val cameraLeft = center.x - cameraWidth / 2f
            val cameraTop = center.y - cameraHeight / 2f
            drawRoundRect(
                color = Color.White.copy(alpha = 0.85f),
                topLeft = Offset(cameraLeft, cameraTop),
                size = Size(cameraWidth, cameraHeight),
                cornerRadius = CornerRadius(9.dp.toPx()),
                style = Stroke(width = 2.dp.toPx()),
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.85f),
                radius = 10.dp.toPx(),
                center = center,
                style = Stroke(width = 2.dp.toPx()),
            )
            drawRoundRect(
                color = Color.White.copy(alpha = 0.85f),
                topLeft = Offset(center.x - 10.dp.toPx(), cameraTop - 5.dp.toPx()),
                size = Size(20.dp.toPx(), 6.dp.toPx()),
                cornerRadius = CornerRadius(3.dp.toPx()),
                style = Stroke(width = 2.dp.toPx()),
            )

            // Scanning line.
            val lineY = inset + (size.height - inset * 2) * scanProgress
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        accent.copy(alpha = 0.9f),
                        Color.Transparent,
                    ),
                ),
                start = Offset(inset, lineY),
                end = Offset(size.width - inset, lineY),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "ES → EN",
                style = MaterialTheme.typography.labelMedium,
                color = accent,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.scanner_placeholder_title),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f),
            )
        }
    }
}

@Composable
private fun FeatureRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
