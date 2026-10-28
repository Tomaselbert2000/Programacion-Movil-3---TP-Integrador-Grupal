package com.example.saferun.domain.usecase

import com.example.saferun.domain.model.HeadingDegree
import com.example.saferun.domain.model.PitchDegree
import com.example.saferun.domain.model.RollDegree
import com.example.saferun.domain.model.SensorData
import io.mockk.every
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.milliseconds

class GetSensorDataUseCaseTest : BaseUseCaseTest<GetSensorDataUseCase>() {
    private val useCase: GetSensorDataUseCase = GetSensorDataUseCase(sensorTracker)

    @Test
    @DisplayName("Debería crear una instancia del caso de uso inyectando dependencia por constructor")
    override fun shouldCreateUseCaseInstanceWithValidData() =
        runTest {
            assertDoesNotThrow { useCase }
        }

    @Test
    @DisplayName("Debería dejar pasar el flujo de datos obtenido desde el respositorio")
    fun shouldPassthroughRepositoryFlow() =
        runTest {
            val expectedSensorData = SensorTestData.defaultSensorData

            val flow = flowWithSingleEmission(expectedSensorData)

            every { sensorTracker.getSensorDataFlow() } returns flow

            val results = useCase.getSensorDataFlow().toList()

            assertAll(
                { assertEquals(1, results.size) },
                { assertEquals(expectedSensorData, results.first()) },
            )
        }

    @Test
    @DisplayName("Debería propagar las excepciones lanzadas desde el repositorio")
    fun shouldPropagateRepositoryExceptions() =
        runTest {
            every { sensorTracker.getSensorDataFlow() } throws IllegalStateException()

            assertFailsWith<IllegalStateException> {
                useCase.getSensorDataFlow().collect { }
            }
        }

    @Test
    @DisplayName("Debería gestionar correctamente la cancelación del flujo de sensores")
    fun shouldHandleFlowCancellation() =
        runTest {
            val expectedSensorData = SensorTestData.defaultSensorData

            val flow = flowWithSingleEmission(expectedSensorData)

            every { sensorTracker.getSensorDataFlow() } returns flow

            val task = launch { useCase.getSensorDataFlow().collect { } }

            assertDoesNotThrow { task.cancel() }
        }

    @Test
    @DisplayName("Debería gestionar correctamente un flujo de datos vacío")
    fun shouldHandleEmptyFlow() =
        runTest {
            val emptyFlow = flow<SensorData> { }

            every { sensorTracker.getSensorDataFlow() } returns emptyFlow

            val results = useCase.getSensorDataFlow().toList()

            assert(results.isEmpty())
        }

    @Test
    @DisplayName("Debería pasar múltiples lecturas de sensores")
    fun shouldPassMultipleSensorReadings() =
        runTest {
            val expectedData1 = SensorTestData.defaultSensorData
            val expectedData2 = SensorTestData.secondSensorData
            val expectedData3 = SensorTestData.thirdSensorData

            val flow = flowWithMultipleEmissions(expectedData1, expectedData2, expectedData3)

            every { sensorTracker.getSensorDataFlow() } returns flow

            val results = useCase.getSensorDataFlow().toList()

            assertAll(
                { assertEquals(3, results.size) },
                { assertEquals(expectedData1, results[0]) },
                { assertEquals(expectedData2, results[1]) },
                { assertEquals(expectedData3, results[2]) },
            )
        }

    @Test
    @DisplayName("Debería manejar lecturas de sensores con delay entre emisiones")
    fun shouldHandleSensorReadingsWithDelay() =
        runTest {
            val data1 =
                SensorData(
                    pitchDegree = PitchDegree(10F),
                    rollDegree = RollDegree(20F),
                    headingDegree = HeadingDegree(90F),
                )

            val data2 =
                SensorData(
                    pitchDegree = PitchDegree(20F),
                    rollDegree = RollDegree(40F),
                    headingDegree = HeadingDegree(100F),
                )

            val flow = flowWithDelay(data1, data2)

            every { sensorTracker.getSensorDataFlow() } returns flow

            val results = useCase.getSensorDataFlow().toList()

            assertEquals(2, results.size)
            assertAll(
                { assertEquals(data1, results[0]) },
                { assertEquals(data2, results[1]) },
            )
        }

    @Test
    @DisplayName("Debería manejar flujos que se completan naturalmente")
    fun shouldHandleCompletableFlow() =
        runTest {
            val data = SensorTestData.defaultSensorData

            val completedFlow = flowWithSingleEmission(data)

            every { sensorTracker.getSensorDataFlow() } returns completedFlow

            val results = useCase.getSensorDataFlow().toList()

            assertEquals(1, results.size)
            assertEquals(data, results[0])
        }

    @Test
    @DisplayName("Debería manejar cancelación antes de que el flujo complete")
    fun shouldHandleCancellationBeforeFlowCompletion() =
        runTest {
            val data1 = SensorTestData.defaultSensorData
            val data2 = SensorTestData.secondSensorData

            val flow = flowWithConditionalEmission(data1, data2, emitSecond = true)

            every { sensorTracker.getSensorDataFlow() } returns flow

            val task = launch { useCase.getSensorDataFlow().collect { } }

            task.cancel()

            assertDoesNotThrow { task.cancel() }
        }

    object SensorTestData {
        val defaultSensorData =
            SensorData(
                pitchDegree = PitchDegree(45F),
                rollDegree = RollDegree(30F),
                headingDegree = HeadingDegree(180F),
            )

        val secondSensorData =
            SensorData(
                pitchDegree = PitchDegree(50F),
                rollDegree = RollDegree(35F),
                headingDegree = HeadingDegree(170F),
            )

        val thirdSensorData =
            SensorData(
                pitchDegree = PitchDegree(55F),
                rollDegree = RollDegree(40F),
                headingDegree = HeadingDegree(160F),
            )
    }

    private fun flowWithSingleEmission(data: SensorData): Flow<SensorData> = flow { emit(data) }

    private fun flowWithMultipleEmissions(
        data1: SensorData,
        data2: SensorData,
        data3: SensorData? = null,
    ): Flow<SensorData> =
        flow {
            emit(data1)
            kotlinx.coroutines.delay(100.milliseconds)
            emit(data2)
            data3?.let {
                kotlinx.coroutines.delay(100.milliseconds)
                emit(it)
            }
        }

    private fun flowWithDelay(
        data1: SensorData,
        data2: SensorData,
        delayMs: Int = 500,
    ): Flow<SensorData> =
        flow {
            emit(data1)
            kotlinx.coroutines.delay(delayMs.milliseconds)
            emit(data2)
        }

    private fun flowWithConditionalEmission(
        data1: SensorData,
        data2: SensorData,
        delayMs: Int = 1000,
        emitSecond: Boolean = true,
    ): Flow<SensorData> =
        flow {
            emit(data1)
            if (emitSecond) {
                kotlinx.coroutines.delay(delayMs.milliseconds)
                emit(data2)
            }
        }
}
