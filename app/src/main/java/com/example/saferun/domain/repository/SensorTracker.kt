package com.example.saferun.domain.repository

import com.example.saferun.domain.model.SensorData
import kotlinx.coroutines.flow.Flow

interface SensorTracker {
    fun getSensorDataFlow(): Flow<SensorData>
}
