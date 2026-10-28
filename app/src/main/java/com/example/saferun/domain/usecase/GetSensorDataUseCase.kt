package com.example.saferun.domain.usecase

import com.example.saferun.domain.model.SensorData
import com.example.saferun.domain.repository.SensorTracker
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSensorDataUseCase
    @Inject
    constructor(
        private val sensorTracker: SensorTracker,
    ) {
        fun getSensorDataFlow(): Flow<SensorData> = sensorTracker.getSensorDataFlow()
    }
