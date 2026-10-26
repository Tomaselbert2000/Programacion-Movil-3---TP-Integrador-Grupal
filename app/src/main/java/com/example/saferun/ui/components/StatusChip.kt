package com.example.saferun.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

enum class StatusTone {
    Positive,
    Neutral,
    Warning,
    Danger,
}

private data class ChipColors(
    val container: Color,
    val content: Color,
)

@Composable
private fun colorsFor(tone: StatusTone): ChipColors {
    val scheme = MaterialTheme.colorScheme
    return when (tone) {
        StatusTone.Positive -> ChipColors(scheme.primaryContainer, scheme.onPrimaryContainer)
        StatusTone.Neutral -> ChipColors(scheme.surfaceVariant, scheme.onSurfaceVariant)
        StatusTone.Warning -> ChipColors(scheme.tertiaryContainer, scheme.onTertiaryContainer)
        StatusTone.Danger -> ChipColors(scheme.errorContainer, scheme.onErrorContainer)
    }
}

/**
 * Pill shaped status indicator. The dot carries the meaning even when the chip
 * is read from a distance, which matters while running or cycling.
 */
@Composable
fun StatusChip(
    text: String,
    modifier: Modifier = Modifier,
    tone: StatusTone = StatusTone.Neutral,
) {
    val colors = colorsFor(tone)
    Row(
        modifier = modifier
            .background(colors.container, CircleShape)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(colors.content, CircleShape),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = colors.content,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
