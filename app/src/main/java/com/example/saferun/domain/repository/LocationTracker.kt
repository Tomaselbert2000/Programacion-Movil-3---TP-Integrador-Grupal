package com.example.saferun.domain.repository

import com.example.saferun.domain.model.LocationData
import kotlinx.coroutines.flow.Flow

interface LocationTracker {
    fun getLocationDataFlow(): Flow<LocationData>
}
