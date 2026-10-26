package com.example.saferun.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.sin

/**
 * Drives the dashboard. For this delivery the telemetry is generated locally
 * (demo mode); the next iteration swaps the source for the real GPS and sensors
 * without changing the screen contract.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState.demo())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private var tickJob: Job? = null
    private var elapsedMillis = INITIAL_ELAPSED
    private val speedSamples = MutableList(8) { INITIAL_AVERAGE }
    private var maxSpeed = 34.6f

    init {
        startTicking()
    }

    fun toggleTracking() {
        if (_uiState.value.isTracking) {
            tickJob?.cancel()
            tickJob = null
            _uiState.update { it.copy(isTracking = false) }
        } else {
            _uiState.update { it.copy(isTracking = true) }
            startTicking()
        }
    }

    fun finishRide() {
        tickJob?.cancel()
        tickJob = null
        _uiState.update { it.copy(isTracking = false, showSummary = true) }
    }

    fun restartRide() {
        elapsedMillis = INITIAL_ELAPSED
        speedSamples.clear()
        repeat(8) { speedSamples.add(INITIAL_AVERAGE) }
        maxSpeed = 34.6f
        _uiState.value = DashboardUiState.demo()
        startTicking()
    }

    fun dismissSummary() {
        _uiState.update { it.copy(showSummary = false) }
    }

    private fun startTicking() {
        tickJob?.cancel()
        tickJob = viewModelScope.launch {
            while (isActive) {
                delay(TICK_MILLIS)
                tick()
            }
        }
    }

    private fun tick() {
        elapsedMillis += TICK_MILLIS
        val seconds = elapsedMillis / 1_000.0

        val speed = (BASE_SPEED + 2.9 * sin(seconds / 3.2) + 0.9 * sin(seconds / 1.15))
            .coerceIn(6.0, 58.0)
            .toFloat()
        val slope = (-3.0 + 1.7 * sin(seconds / 5.0)).toFloat()
        val heading = ((312.0 + 7.0 * sin(seconds / 9.0)) % 360.0).toFloat()
        val accuracy = (4.2 + 1.3 * sin(seconds / 4.0)).toFloat().coerceAtLeast(2.5f)

        speedSamples += speed
        if (speedSamples.size > MAX_SAMPLES) {
            speedSamples.removeAt(0)
        }
        val average = speedSamples.average().toFloat()
        maxSpeed = maxOf(maxSpeed, speed)

        val distanceKm = average * (elapsedMillis / 3_600_000.0)
        val progress = (distanceKm / DemoRoute.totalKm).coerceIn(0.02, 1.0).toFloat()

        _uiState.update { current ->
            current.copy(
                speedKmh = speed,
                averageSpeedKmh = average,
                maxSpeedKmh = maxSpeed,
                slopeDegrees = slope,
                headingDegrees = heading,
                distanceKm = distanceKm,
                elapsedMillis = elapsedMillis,
                accuracyMeters = accuracy,
                route = DemoRoute.reveal(progress),
            )
        }
    }

    private companion object {
        const val TICK_MILLIS = 1_000L
        const val INITIAL_ELAPSED = 132_000L
        const val INITIAL_AVERAGE = 24.1f
        const val BASE_SPEED = 28.5
        const val MAX_SAMPLES = 180
    }
}
