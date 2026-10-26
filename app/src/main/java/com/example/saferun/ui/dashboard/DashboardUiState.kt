package com.example.saferun.ui.dashboard

import androidx.compose.runtime.Immutable
import com.example.saferun.ui.map.RoutePoint
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class GpsState {
    Active,
    Searching,
    Disabled,
}

@Immutable
data class DashboardUiState(
    val isTracking: Boolean = false,
    val speedKmh: Float = 0f,
    val gaugeMaxKmh: Float = 60f,
    val maxSpeedKmh: Float = 0f,
    val averageSpeedKmh: Float = 0f,
    val slopeDegrees: Float = 0f,
    val headingDegrees: Float = 0f,
    val distanceKm: Double = 0.0,
    val elapsedMillis: Long = 0L,
    val gpsState: GpsState = GpsState.Searching,
    val accuracyMeters: Float? = null,
    val route: List<RoutePoint> = emptyList(),
    val showSummary: Boolean = false,
    val isDemo: Boolean = true,
) {
    companion object {
        fun demo(): DashboardUiState {
            val elapsed = 132_000L
            val average = 24.1f
            val distance = average * (elapsed / 3_600_000.0)
            return DashboardUiState(
                isTracking = true,
                speedKmh = 28.5f,
                maxSpeedKmh = 34.6f,
                averageSpeedKmh = average,
                slopeDegrees = -3f,
                headingDegrees = 312f,
                distanceKm = distance,
                elapsedMillis = elapsed,
                gpsState = GpsState.Active,
                accuracyMeters = 4.6f,
                route = DemoRoute.reveal((distance / DemoRoute.totalKm).toFloat()),
                showSummary = false,
                isDemo = true,
            )
        }
    }
}

/**
 * Deterministic route around the Palermo parks in Buenos Aires. Used by the demo
 * mode until the real GPS tracking lands, so presentations never depend on signal.
 */
object DemoRoute {
    private const val BASE_LAT = -34.5711
    private const val BASE_LNG = -58.4233
    private const val STEPS = 72

    val points: List<RoutePoint> = buildList {
        for (index in 0..STEPS) {
            val t = index / STEPS.toDouble()
            val angle = t * 2 * PI
            val latitude = BASE_LAT + 0.0038 * sin(angle) + 0.0006 * sin(angle * 3)
            val longitude = BASE_LNG + 0.0052 * (cos(angle) - 1.0) + 0.0007 * sin(angle * 2)
            add(RoutePoint(latitude = latitude, longitude = longitude))
        }
    }

    val totalKm: Double by lazy { polylineLengthKm(points) }

    fun reveal(progress: Float): List<RoutePoint> {
        val clamped = progress.coerceIn(0.03f, 1f)
        val count = (points.size * clamped).toInt().coerceIn(2, points.size)
        return points.subList(0, count)
    }
}

/** Total length of a polyline in kilometres using the haversine formula. */
fun polylineLengthKm(points: List<RoutePoint>): Double {
    var totalMeters = 0.0
    for (index in 1 until points.size) {
        totalMeters += haversineMeters(points[index - 1], points[index])
    }
    return totalMeters / 1_000.0
}

private fun haversineMeters(from: RoutePoint, to: RoutePoint): Double {
    val earthRadius = 6_371_000.0
    val lat1 = Math.toRadians(from.latitude)
    val lat2 = Math.toRadians(to.latitude)
    val deltaLat = lat2 - lat1
    val deltaLng = Math.toRadians(to.longitude - from.longitude)
    val a = sin(deltaLat / 2) * sin(deltaLat / 2) +
        cos(lat1) * cos(lat2) * sin(deltaLng / 2) * sin(deltaLng / 2)
    return earthRadius * 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
}

fun formatDuration(millis: Long): String {
    val totalSeconds = (millis / 1_000).coerceAtLeast(0)
    val hours = totalSeconds / 3_600
    val minutes = (totalSeconds % 3_600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }
}

fun formatDecimal(value: Float, decimals: Int = 1): String =
    String.format(Locale.getDefault(), "%.${decimals}f", value)
