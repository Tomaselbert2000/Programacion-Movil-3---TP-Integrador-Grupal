package com.example.saferun.domain.usecase

import com.example.saferun.domain.model.LocationData
import com.example.saferun.domain.model.Speed
import com.example.saferun.domain.usecase.AverageSpeedTestConstants.FINAL_SPEED
import com.example.saferun.domain.usecase.AverageSpeedTestConstants.GPS_AVAILABLE
import com.example.saferun.domain.usecase.AverageSpeedTestConstants.INITIAL_SPEED
import com.example.saferun.domain.usecase.AverageSpeedTestConstants.INITIAL_TIMESTAMP
import com.example.saferun.domain.usecase.AverageSpeedTestConstants.LAST_TIMESTAMP
import com.example.saferun.domain.usecase.AverageSpeedTestConstants.LATITUDE
import com.example.saferun.domain.usecase.AverageSpeedTestConstants.LONGITUDE
import io.mockk.every
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class AverageSpeedUseCaseTest : BaseUseCaseTest<AverageSpeedUseCase>() {
    val averageSpeedUseCase = AverageSpeedUseCase(locationTracker)

    @Test
    @DisplayName("Debería crear una instancia del caso de uso inyectando por constructor su dependencia")
    override fun shouldCreateUseCaseInstanceWithValidData() =
        runTest {
            assertDoesNotThrow { averageSpeedUseCase }
        }

    @Test
    @DisplayName("Dadas 2 ubicaciones obtenidas como secuencia, debería poder calcular la velocidad promedio del usuario")
    fun shouldGetAverageSpeedWithValidLocations() =
        runTest {
            val location1 =
                LocationData(
                    speed = Speed(INITIAL_SPEED),
                    latitude = LATITUDE,
                    longitude = LONGITUDE,
                    isGpsAvailable = GPS_AVAILABLE,
                    timestamp = INITIAL_TIMESTAMP,
                )

            val location2 =
                LocationData(
                    speed = Speed(FINAL_SPEED),
                    latitude = LATITUDE,
                    longitude = LONGITUDE,
                    isGpsAvailable = GPS_AVAILABLE,
                    timestamp = LAST_TIMESTAMP,
                )

            val expectedAverageSpeedConvertedToKilometersPerHour = 15.0 * 3.6

            val flow = flowOf(location1, location2)

            every { locationTracker.getLocationDataFlow() } returns flow

            val result = averageSpeedUseCase.calculateAverageSpeed()

            val emissionsList = result.toList()

            assertEquals(
                expectedAverageSpeedConvertedToKilometersPerHour,
                emissionsList.last().registeredSpeed,
            )
        }
}

object AverageSpeedTestConstants {
    const val INITIAL_SPEED = 10.0f
    const val FINAL_SPEED = 20.0f
    const val INITIAL_TIMESTAMP = 1L
    const val LAST_TIMESTAMP = 10000L
    const val LATITUDE = -34.6037
    const val LONGITUDE = -58.3816
    const val GPS_AVAILABLE = true
}
