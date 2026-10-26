package com.example.saferun.domain.usecase

import com.example.saferun.domain.model.HeadingDegree
import com.example.saferun.domain.model.PitchDegree
import com.example.saferun.domain.model.RollDegree
import com.example.saferun.domain.model.SensorData
import com.example.saferun.domain.usecase.SensorDataUseCaseTestConstants.HEADING
import com.example.saferun.domain.usecase.SensorDataUseCaseTestConstants.PITCH
import com.example.saferun.domain.usecase.SensorDataUseCaseTestConstants.ROLL
import io.mockk.every
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class ObserveSensorUpdatesUseCaseTest : BaseUseCaseTest<ObserveLocationUpdateUseCase>() {
    val sensorUpdatesUseCase = ObserveSensorUpdatesUseCase(sensorTracker)

    @Test
    @DisplayName("Debería crear una instancia del caso de uso mediante inyección de dependencias")
    override fun shouldCreateUseCaseInstanceWithValidData() {
        assertDoesNotThrow { sensorUpdatesUseCase }
    }

    @Test
    @DisplayName("Debería leer correctamente los datos de sensores")
    fun shouldReadSensorDataCorrectly() =
        runTest {
            val mockSensorData =
                SensorData(
                    pitchDegree = PitchDegree(PITCH),
                    rollDegree = RollDegree(ROLL),
                    headingDegree = HeadingDegree(HEADING),
                )

            val sensorFlow = flowOf(mockSensorData)

            every { sensorTracker.getSensorDataFlow() } returns sensorFlow

            val result = sensorUpdatesUseCase.observeSensorDataUpdates()

            val lastItem = result.toList().last()

            assertAll(
                { assertEquals(lastItem.pitchDegree.value, PITCH) },
                { assertEquals(lastItem.rollDegree.value, ROLL) },
                { assertEquals(lastItem.headingDegree.value, HEADING) },
            )
        }

    @Test
    @DisplayName("Debería verificar que se capture una excepción si la lectura de sensores falla por cualquier motivo")
    fun shouldHandleExceptions() =
        runTest {
            every { sensorTracker.getSensorDataFlow() } returns
                flow {
                    throw IllegalStateException("Error en el flow del sensor")
                }

            assertThrows<IllegalStateException> {
                sensorUpdatesUseCase.observeSensorDataUpdates().collect { }
            }
        }
}

object SensorDataUseCaseTestConstants {
    const val PITCH = 15.0f
    const val ROLL = -5.0f
    const val HEADING = 90.0f
}
