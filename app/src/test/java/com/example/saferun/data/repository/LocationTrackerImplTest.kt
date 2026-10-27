package com.example.saferun.data.repository

import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class LocationTrackerImplTest : BaseRepositoryTest() {
    val mockLocationProvider = loadMockedLocationProvider()
    val mockLocationTracker = LocationTrackerImpl(mockLocationProvider)

    @Nested
    inner class Constructor {
        @Test
        @Suppress("ktlint:standard:max-line-length")
        @DisplayName(
            "Debería crearse una instancia de LocationTracker inyectando correctamente un FusedLocationProviderClient como constructor",
        )
        fun shouldCreateALocationTrackerImplInstanceWithFusedLocationProviderClient() {
            assertDoesNotThrow { LocationTrackerImpl(mockLocationProvider) }
        }
    }

    @Nested
    inner class LocationDataFlow {
        @OptIn(ExperimentalCoroutinesApi::class)
        @Test
        @DisplayName(
            "Debería emitir un flujo de LocationData con coordenadas válidas si el GPS está disponible",
        )
        fun shouldEmitLocationDataWithValidCoordinates() =
            runTest {
                val callbackSlot = createCallbackSlot()

                mockLocationUpdates(callbackSlot)

                val mockedLocation =
                    mockk<Location> {
                        every { latitude } returns 20.2
                        every { longitude } returns 120.9
                        every { speed } returns 60f
                    }

                val deferredLocation = async { mockLocationTracker.getLocationDataFlow().first() }

                runCurrent()

                val mockLocationResult =
                    mockk<LocationResult> {
                        every { lastLocation } returns mockedLocation
                    }
                callbackSlot.captured.onLocationResult(mockLocationResult)

                val emittedLocation = deferredLocation.await()

                assertAll(
                    { assertEquals(20.2, emittedLocation.latitude) },
                    { assertEquals(120.9, emittedLocation.longitude) },
                    { assertEquals(60f, emittedLocation.speed.value) },
                )
            }

        @Test
        @DisplayName("Debería cerrar correctamente el flujo de ubicación si se detecta que no hay permisos de acceso a GPS")
        fun shouldCloseLocationFlowOnSecurityException() =
            runTest {
                val callbackSlot = createCallbackSlot()

                every {
                    mockLocationProvider.requestLocationUpdates(
                        any<LocationRequest>(),
                        capture(callbackSlot),
                        any<Looper>(),
                    )
                } throws SecurityException("Permiso denegado")

                assertThrows<SecurityException> {
                    mockLocationTracker.getLocationDataFlow().collect { }
                }
            }

        @OptIn(ExperimentalCoroutinesApi::class)
        @Test
        @DisplayName("Debería dejar de consumir datos de ubicación si el usuario cierra la pantalla")
        fun shouldCancelFlowLocationOnExit() =
            runTest {
                val callbackSlot = createCallbackSlot()

                mockLocationUpdates(callbackSlot)

                val task =
                    launch {
                        mockLocationTracker.getLocationDataFlow().collect { }
                    }

                runCurrent()

                task.cancel()

                runCurrent()

                verify(
                    verifyBlock = { mockLocationProvider.removeLocationUpdates(callbackSlot.captured) },
                    exactly = 1,
                )
            }
    }

    fun createCallbackSlot(): CapturingSlot<LocationCallback> = slot<LocationCallback>()

    fun loadMockedLocationProvider(): FusedLocationProviderClient = mockk<FusedLocationProviderClient>(relaxed = true)

    fun mockLocationUpdates(callbackSlot: CapturingSlot<LocationCallback>) {
        every {
            mockLocationProvider.requestLocationUpdates(
                any<LocationRequest>(),
                capture(callbackSlot),
                any<Looper>(),
            )
        } returns mockk(relaxed = true)
    }
}
