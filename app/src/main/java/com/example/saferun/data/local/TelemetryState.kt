package com.example.saferun.data.local

data class TelemetryState(
    val locationData: LocationData,
    val sensorData: SensorData,
    val isTrackingActive: Boolean = false,
    val hasLocationPermission: Boolean = false,
)
