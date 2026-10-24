package com.example.saferun.data.repository

import android.location.Location
import android.os.Looper
import com.example.saferun.data.repository.LocationRequestSettings.INTERVAL_MILLIS
import com.example.saferun.data.repository.LocationRequestSettings.MIN_INTERVAL_MILLIS
import com.example.saferun.data.repository.LocationRequestSettings.PRIORITY
import com.example.saferun.domain.model.LocationData
import com.example.saferun.domain.model.Speed
import com.example.saferun.domain.repository.LocationTracker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class LocationTrackerImpl
    @Inject
    constructor(
        private val locationProvider: FusedLocationProviderClient,
    ) : LocationTracker {
        override fun getLocationDataFlow(): Flow<LocationData> =
            callbackFlow {
                val callback =
                    object : LocationCallback() {
                        override fun onLocationResult(result: LocationResult) {
                            result.lastLocation?.let { location ->
                                val locationData =
                                    mapLocationDataInstanceFromLocationResult(location)

                                trySend(locationData)
                            }
                        }
                    }
                val request = createLocationRequestWithDefaultSettings()

                try {
                    locationProvider
                        .requestLocationUpdates(
                            request,
                            callback,
                            Looper.getMainLooper(),
                        ).addOnFailureListener { exception -> close(exception) }
                } catch (e: SecurityException) {
                    close(e)
                }

                awaitClose { locationProvider.removeLocationUpdates(callback) }
            }

        private fun createLocationRequestWithDefaultSettings(): LocationRequest =
            LocationRequest
                .Builder(PRIORITY, INTERVAL_MILLIS)
                .setIntervalMillis(MIN_INTERVAL_MILLIS)
                .build()

        private fun mapLocationDataInstanceFromLocationResult(location: Location): LocationData =
            LocationData(
                speed = Speed(location.speed),
                latitude = location.latitude,
                longitude = location.longitude,
                isGpsAvailable = true,
                timestamp = System.currentTimeMillis(),
            )
    }

object LocationRequestSettings {
    const val PRIORITY = Priority.PRIORITY_HIGH_ACCURACY
    const val INTERVAL_MILLIS = 5000L
    const val MIN_INTERVAL_MILLIS = 2000L
}
