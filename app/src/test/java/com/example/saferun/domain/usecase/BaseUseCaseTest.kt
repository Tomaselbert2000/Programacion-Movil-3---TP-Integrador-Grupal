package com.example.saferun.domain.usecase

import com.example.saferun.domain.repository.LocationTracker
import com.example.saferun.domain.repository.SensorTracker
import io.mockk.mockk

abstract class BaseUseCaseTest<T : Any> {
    protected val locationTracker: LocationTracker = mockk<LocationTracker>()
    protected val sensorTracker: SensorTracker = mockk<SensorTracker>()

    abstract fun shouldCreateUseCaseInstanceWithValidData()
}
