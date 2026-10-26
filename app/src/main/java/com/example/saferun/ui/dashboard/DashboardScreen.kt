package com.example.saferun.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.saferun.R
import com.example.saferun.ui.components.CompassDial
import com.example.saferun.ui.components.MetricTile
import com.example.saferun.ui.components.SafeRunCard
import com.example.saferun.ui.components.SectionLabel
import com.example.saferun.ui.components.SlopeIndicator
import com.example.saferun.ui.components.SpeedometerGauge
import com.example.saferun.ui.components.StatusChip
import com.example.saferun.ui.components.StatusTone
import com.example.saferun.ui.components.cardinalName
import com.example.saferun.ui.components.slopeColor
import com.example.saferun.ui.map.RouteMap
import com.example.saferun.ui.theme.SafeRunTheme
import java.util.Locale
import kotlin.math.tan

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    onOpenSettings: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    DashboardContent(
        state = state,
        modifier = modifier,
        onOpenSettings = onOpenSettings,
        onToggleTracking = viewModel::toggleTracking,
        onFinish = viewModel::finishRide,
        onRestart = viewModel::restartRide,
        onDismissSummary = viewModel::dismissSummary,
    )
}

@Composable
private fun DashboardContent(
    state: DashboardUiState,
    modifier: Modifier = Modifier,
    onOpenSettings: () -> Unit = {},
    onToggleTracking: () -> Unit = {},
    onFinish: () -> Unit = {},
    onRestart: () -> Unit = {},
    onDismissSummary: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(10.dp))
        DashboardHeader(onOpenSettings = onOpenSettings)
        Spacer(Modifier.height(16.dp))
        MapCard(state = state)
        Spacer(Modifier.height(14.dp))
        SpeedCard(state = state)
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MetricTile(
                label = stringResource(R.string.metric_average),
                value = formatDecimal(state.averageSpeedKmh),
                unit = stringResource(R.string.speed_unit),
                modifier = Modifier
                    .weight(1f)
                    .height(144.dp),
            )
            MetricTile(
                label = stringResource(R.string.metric_slope),
                value = formatSignedDegrees(state.slopeDegrees),
                unit = null,
                modifier = Modifier
                    .weight(1f)
                    .height(144.dp),
                valueColor = slopeColor(state.slopeDegrees),
                footer = {
                    val label = slopeLabel(state.slopeDegrees)
                    val percent = formatDecimal((tan(Math.toRadians(state.slopeDegrees.toDouble())) * 100).toFloat())
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        SlopeIndicator(
                            degrees = state.slopeDegrees,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(22.dp),
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "$label · $percent %",
                            style = MaterialTheme.typography.bodySmall,
                            color = slopeColor(state.slopeDegrees),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                },
            )
        }
        Spacer(Modifier.height(12.dp))
        CompassCard(state = state)
        Spacer(Modifier.height(12.dp))
        TripStatsCard(state = state)
        Spacer(Modifier.height(18.dp))
        RideActions(
            isTracking = state.isTracking,
            onToggleTracking = onToggleTracking,
            onFinish = onFinish,
        )
        Spacer(Modifier.height(24.dp))
    }

    if (state.showSummary) {
        RideSummaryDialog(
            state = state,
            onRestart = onRestart,
            onDismiss = onDismissSummary,
        )
    }
}

@Composable
private fun DashboardHeader(onOpenSettings: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary,
                        ),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "S",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = stringResource(R.string.dashboard_tagline),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(
            onClick = onOpenSettings,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = stringResource(R.string.nav_settings),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun MapCard(state: DashboardUiState) {
    val shape = RoundedCornerShape(28.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(168.dp)
            .clip(shape)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape),
    ) {
        RouteMap(
            points = state.route,
            currentPosition = state.route.lastOrNull(),
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Black.copy(alpha = 0.32f),
                        0.38f to Color.Transparent,
                        0.68f to Color.Transparent,
                        1f to Color.Black.copy(alpha = 0.62f),
                    ),
                ),
        )
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StatusChip(text = gpsLabel(state.gpsState), tone = gpsTone(state.gpsState))
        }
        StatusChip(
            text = stringResource(R.string.dashboard_map_demo),
            tone = StatusTone.Neutral,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp),
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.dashboard_map_title),
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                )
                Text(
                    text = "© OpenStreetMap",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.72f),
                )
            }
            Text(
                text = "${formatDecimal(state.distanceKm.toFloat(), 2)} km",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
            )
        }
    }
}

@Composable
private fun SpeedCard(state: DashboardUiState) {
    SafeRunCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SectionLabel(stringResource(R.string.dashboard_speed_label))
            StatusChip(
                text = stringResource(
                    if (state.isTracking) R.string.dashboard_live else R.string.dashboard_paused,
                ),
                tone = if (state.isTracking) StatusTone.Positive else StatusTone.Warning,
            )
        }
        SpeedometerGauge(
            speedKmh = state.speedKmh,
            maxSpeedKmh = state.gaugeMaxKmh,
            unit = stringResource(R.string.speed_unit),
            maxSpeedLabel = stringResource(R.string.speed_max, formatDecimal(state.maxSpeedKmh)),
            modifier = Modifier
                .fillMaxWidth()
                .height(212.dp)
                .padding(top = 4.dp),
        )
    }
}

@Composable
private fun CompassCard(state: DashboardUiState) {
    SafeRunCard(contentPadding = PaddingValues(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CompassDial(
                headingDegrees = state.headingDegrees,
                modifier = Modifier.size(104.dp),
            )
            Spacer(Modifier.width(18.dp))
            Column(modifier = Modifier.weight(1f)) {
                SectionLabel(stringResource(R.string.metric_heading))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = cardinalName(state.headingDegrees),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.width(7.dp))
                    Text(
                        text = stringResource(
                            R.string.compass_degrees,
                            formatDecimal(((state.headingDegrees % 360f) + 360f) % 360f, 0),
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 5.dp),
                    )
                }
                Text(
                    text = stringResource(R.string.compass_current_heading),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun TripStatsCard(state: DashboardUiState) {
    SafeRunCard(contentPadding = PaddingValues(vertical = 16.dp, horizontal = 18.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatBlock(
                label = stringResource(R.string.stat_time),
                value = formatDuration(state.elapsedMillis),
                modifier = Modifier.weight(1f),
            )
            VerticalDivider(
                modifier = Modifier.height(34.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
            )
            StatBlock(
                label = stringResource(R.string.stat_distance),
                value = "${formatDecimal(state.distanceKm.toFloat(), 2)} km",
                modifier = Modifier.weight(1f),
            )
            VerticalDivider(
                modifier = Modifier.height(34.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
            )
            StatBlock(
                label = stringResource(R.string.stat_signal),
                value = state.accuracyMeters?.let {
                    stringResource(R.string.gps_accuracy, formatDecimal(it))
                } ?: stringResource(R.string.gps_no_accuracy),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun StatBlock(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun RideActions(
    isTracking: Boolean,
    onToggleTracking: () -> Unit,
    onFinish: () -> Unit,
) {
    val toggleDescription = stringResource(
        if (isTracking) R.string.action_pause else R.string.action_resume,
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FilledTonalButton(
            onClick = onToggleTracking,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(18.dp))
                .semantics { contentDescription = toggleDescription },
            shape = RoundedCornerShape(18.dp),
            contentPadding = PaddingValues(0.dp),
        ) {
            TransportGlyph(
                icon = if (isTracking) Glyph.Pause else Glyph.Play,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(18.dp),
            )
        }
        Button(
            onClick = onFinish,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
            ),
        ) {
            TransportGlyph(
                icon = Glyph.Stop,
                tint = MaterialTheme.colorScheme.onError,
                modifier = Modifier.size(14.dp),
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.action_finish),
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun RideSummaryDialog(
    state: DashboardUiState,
    onRestart: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.summary_title)) },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.summary_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(14.dp))
                SummaryRow(stringResource(R.string.summary_time), formatDuration(state.elapsedMillis))
                SummaryRow(
                    stringResource(R.string.summary_distance),
                    "${formatDecimal(state.distanceKm.toFloat(), 2)} km",
                )
                SummaryRow(
                    stringResource(R.string.summary_average),
                    "${formatDecimal(state.averageSpeedKmh)} km/h",
                )
                SummaryRow(
                    stringResource(R.string.summary_max),
                    "${formatDecimal(state.maxSpeedKmh)} km/h",
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onRestart) {
                Text(stringResource(R.string.summary_new_ride))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.summary_close))
            }
        },
    )
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun gpsLabel(state: GpsState): String = stringResource(
    when (state) {
        GpsState.Active -> R.string.gps_active
        GpsState.Searching -> R.string.gps_searching
        GpsState.Disabled -> R.string.gps_disabled
    },
)

@Composable
private fun gpsTone(state: GpsState): StatusTone = when (state) {
    GpsState.Active -> StatusTone.Positive
    GpsState.Searching -> StatusTone.Warning
    GpsState.Disabled -> StatusTone.Danger
}

@Composable
private fun slopeLabel(degrees: Float): String = stringResource(
    when {
        degrees > 1.5f -> R.string.slope_up
        degrees < -1.5f -> R.string.slope_down
        else -> R.string.slope_flat
    },
)

private fun formatSignedDegrees(value: Float): String =
    String.format(Locale.getDefault(), "%+.1f°", value)

private enum class Glyph {
    Play,
    Pause,
    Stop,
}

@Composable
private fun TransportGlyph(
    icon: Glyph,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        when (icon) {
            Glyph.Play -> {
                val path = Path().apply {
                    moveTo(1f, 0f)
                    lineTo(size.width, size.height / 2f)
                    lineTo(1f, size.height)
                    close()
                }
                drawPath(path, color = tint)
            }

            Glyph.Pause -> {
                val barWidth = size.width * 0.3f
                val radius = CornerRadius(1.5.dp.toPx())
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(0f, 0f),
                    size = Size(barWidth, size.height),
                    cornerRadius = radius,
                )
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(size.width - barWidth, 0f),
                    size = Size(barWidth, size.height),
                    cornerRadius = radius,
                )
            }

            Glyph.Stop -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset.Zero,
                    size = Size(size.width, size.height),
                    cornerRadius = CornerRadius(2.dp.toPx()),
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 411, heightDp = 891)
@Composable
private fun DashboardPreview() {
    SafeRunTheme {
        DashboardContent(state = DashboardUiState.demo())
    }
}
