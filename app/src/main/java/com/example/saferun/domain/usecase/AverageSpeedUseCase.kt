package com.example.saferun.domain.usecase

import com.example.saferun.domain.dto.AverageSpeedData
import com.example.saferun.domain.model.LocationData
import com.example.saferun.domain.repository.LocationTracker
import com.example.saferun.domain.usecase.DistanceCalculationConstants.EARTH_RADIUS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Math.toRadians
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class AverageSpeedUseCase
    @Inject
    constructor(
        private val locationTracker: LocationTracker,
    ) {
        fun calculateAverageSpeed(): Flow<AverageSpeedData> =
            flow {
                var counter = 0
                var totalSpeed = 0.0
                var totalKilometers = 0.0

                var lastLocation: LocationData? = null

                locationTracker.getLocationDataFlow().collect { currentLocation ->

                    counter++
                    totalSpeed += currentLocation.speed.value

                    if (lastLocation != null) {
                        totalKilometers += calculateDistance(lastLocation!!, currentLocation)
                    }

                    lastLocation = currentLocation

                    val currentSpeedKilometersPerHour = (totalSpeed / counter) * 3.6

                    emit(
                        AverageSpeedData(currentSpeedKilometersPerHour, totalKilometers),
                    )
                }
            }

        private fun calculateDistance(
            from: LocationData,
            to: LocationData,
        ): Double {
            val lat1Rad = toRadians(from.latitude)
            val lat2Rad = toRadians(to.latitude)
            val deltaLatRad = toRadians(to.latitude - from.latitude)
            val deltaLongRad = toRadians(to.longitude - from.longitude)
            val a =
                sin(deltaLatRad / 2).pow(2) +
                    cos(lat1Rad) * cos(lat2Rad) *
                    sin(deltaLongRad / 2).pow(2)

            val c = 2 * atan2(sqrt(a), sqrt(1 - a))

            return EARTH_RADIUS * c
        }
    }

object DistanceCalculationConstants {
    const val EARTH_RADIUS = 6371.0
}
