package com.example.saferun.domain.usecase

import com.example.saferun.domain.repository.LocationTracker
import io.mockk.mockk

abstract class BaseUseCaseTest<T : Any> {
    protected val locationTracker: LocationTracker = mockk<LocationTracker>()

    abstract fun shouldCreateUseCaseInstanceWithValidData()
}
