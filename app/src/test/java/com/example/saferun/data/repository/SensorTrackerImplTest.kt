package com.example.saferun.data.repository

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.saferun.domain.model.HeadingDegree
import com.example.saferun.domain.model.PitchDegree
import com.example.saferun.domain.model.RollDegree
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import java.lang.reflect.Field

class SensorTrackerImplTest : BaseRepositoryTest() {
    val mockSensor: Sensor = mockk<Sensor>()
    val mockSensorManager = mockk<SensorManager>()
    val mockedEvent: SensorEvent = mockk<SensorEvent>()
    val sensorTrackerImpl = SensorTrackerImpl(mockSensorManager)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("Debería capturar correctamente la información de un evento de sensor")
    fun shouldCreateSensorDataInstanceWithValidData() =
        runTest {
            every { mockSensorManager.getDefaultSensor(any()) } returns mockSensor

            val listenerSlot = createCallbackListener()

            every {
                mockSensorManager.registerListener(
                    capture(listenerSlot),
                    any(),
                    any(),
                )
            } returns true

            every { mockSensorManager.unregisterListener(any<SensorEventListener>()) } just runs

            val valuesField = SensorEvent::class.java.getField("values")

            setupMockedEventByJavaReflexion(valuesField, mockedEvent)

            val sensorEventRead = async { sensorTrackerImpl.getSensorDataFlow().first() }

            runCurrent()

            listenerSlot.captured.onSensorChanged(mockedEvent)

            val result = sensorEventRead.await()

            assertAll(
                { assertEquals(result.headingDegree, HeadingDegree(90.0f)) },
                { assertEquals(result.pitchDegree, PitchDegree(15.0f)) },
                { assertEquals(result.rollDegree, RollDegree(-5.0f)) },
            )
        }

    @Test
    @DisplayName("Debería arrojar IllegalStateException si el dispositivo no cuenta con sensor o el mismo no se encuentra disponible")
    fun shouldThrowIllegalStateExceptionIfSensorsAreNotAvailable() =
        runTest {
            every { mockSensorManager.getDefaultSensor(any()) } returns null
            assertThrows<IllegalStateException> { sensorTrackerImpl.getSensorDataFlow().first() }
        }

    private fun createCallbackListener() = slot<SensorEventListener>()

    private fun setupMockedEventByJavaReflexion(
        valuesField: Field,
        mockedEvent: SensorEvent,
    ) {
        valuesField.set(
            mockedEvent,
            floatArrayOf(
                Math.toRadians(90.0).toFloat(),
                Math.toRadians(15.0).toFloat(),
                Math.toRadians(-5.0).toFloat(),
            ),
        )
    }
}
