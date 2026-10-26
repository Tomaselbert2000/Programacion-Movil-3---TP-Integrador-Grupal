package com.example.saferun.domain.usecase

import com.example.saferun.domain.model.LocationData
import com.example.saferun.domain.repository.LocationTracker
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveLocationUpdateUseCase
    @Inject
    constructor(
        private val locationTracker: LocationTracker,
    ) {
        fun observeLocationUpdates(): Flow<LocationData> = locationTracker.getLocationDataFlow()
    }
