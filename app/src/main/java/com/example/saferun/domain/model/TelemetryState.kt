package com.example.saferun.domain.model

data class TelemetryState(
    val locationData: LocationData,
    val sensorData: SensorData,
    val isTrackingActive: Boolean = false,
    val hasLocationPermission: Boolean = false,
)
