package com.example.saferun.domain.usecase

import com.example.saferun.domain.factory.TestDataFactory
import io.mockk.every
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class ObserveLocationUpdatesUseCaseTest : BaseUseCaseTest<ObserveLocationUpdateUseCase>() {
    val locationUpdateUseCase = ObserveLocationUpdateUseCase(locationTracker)
    val defaultLocation = TestDataFactory.buildDefaultLocationDataInstance()

    @Test
    @DisplayName("Debería crear una instancia del caso de uso mediante inyección de dependencias")
    override fun shouldCreateUseCaseInstanceWithValidData() {
        runTest {
            assertDoesNotThrow { locationUpdateUseCase }
        }
    }

    @Test
    @DisplayName("Al observar las ubicaciones, si el tracker emite datos, debería retornar la primera ubicación intacta")
    fun observeLocationUpdates_whenTrackerEmitsData_returnsFirstLocation() =
        runTest {
            mockDefaultLocation()

            val flow = locationUpdateUseCase.observeLocationUpdates()

            val firstItem = flow.first()

            assertEquals(firstItem, defaultLocation)
        }

    private fun mockDefaultLocation() {
        every { locationTracker.getLocationDataFlow() } returns flowOf(defaultLocation)
    }
}
