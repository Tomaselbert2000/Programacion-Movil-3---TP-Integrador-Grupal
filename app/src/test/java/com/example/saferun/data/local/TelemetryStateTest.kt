package com.example.saferun.data.local

import com.example.saferun.data.local.factory.TestDataFactory.Companion.buildDefaultLocationDataInstance
import com.example.saferun.data.local.factory.TestDataFactory.Companion.buildDefaultSensorDataInstance
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class TelemetryStateTest {
    @Nested
    inner class BasicConstructor {
        @Test
        @DisplayName("Debería crearse una instancia de TelemetryState con datos correctos")
        fun shouldCreateATelemetryStateInstanceWithValidData() {
            TelemetryState(
                locationData = buildDefaultLocationDataInstance(),
                sensorData = buildDefaultSensorDataInstance(),
                isTrackingActive = false,
                hasLocationPermission = false,
            )
        }
    }

    @Nested
    inner class DefaultValues {
        @Test
        @DisplayName("Debería crearse una instancia de TelemetryState con valores por defecto para isTrackingActive")
        fun shouldCreateATelemetryStateInstanceWithDefaultTrackingActiveValue() {
            val telemetryState =
                TelemetryState(
                    locationData = buildDefaultLocationDataInstance(),
                    sensorData = buildDefaultSensorDataInstance(),
                    isTrackingActive = false,
                    hasLocationPermission = false,
                )
            assertEquals(false, telemetryState.isTrackingActive)
        }

        @Test
        @DisplayName("Debería crearse una instancia de TelemetryState con valores por defecto para hasLocationPermission")
        fun shouldCreateATelemetryStateInstanceWithDefaultLocationPermissionValue() {
            val telemetryState =
                TelemetryState(
                    locationData = buildDefaultLocationDataInstance(),
                    sensorData = buildDefaultSensorDataInstance(),
                    isTrackingActive = false,
                    hasLocationPermission = false,
                )
            assertEquals(false, telemetryState.hasLocationPermission)
        }

        @Test
        @DisplayName("Debería crearse una instancia de TelemetryState con valores por defecto para todas las propiedades")
        fun shouldCreateATelemetryStateInstanceWithAllDefaultValue() {
            val telemetryState =
                TelemetryState(
                    locationData = buildDefaultLocationDataInstance(),
                    sensorData = buildDefaultSensorDataInstance(),
                )
            assertEquals(false, telemetryState.isTrackingActive)
            assertEquals(false, telemetryState.hasLocationPermission)
        }

        @Test
        @DisplayName("La instancia creada con valores por defecto debe tener isTrackingActive como false")
        fun shouldHaveDefaultTrackingActiveAsFalse() {
            val telemetryState =
                TelemetryState(
                    locationData = buildDefaultLocationDataInstance(),
                    sensorData = buildDefaultSensorDataInstance(),
                    isTrackingActive = false,
                    hasLocationPermission = false,
                )
            assertEquals(false, telemetryState.isTrackingActive)
        }
    }

    @Nested
    inner class ExplicitValues {
        @ParameterizedTest
        @ValueSource(booleans = [true, false])
        @DisplayName("Debería crearse una instancia de TelemetryState con el valor explícito del primer booleano")
        fun shouldCreateATelemetryStateInstanceWithExplicitFirstBooleanValue(isTrackingActive: Boolean) {
            val telemetryState =
                TelemetryState(
                    locationData = buildDefaultLocationDataInstance(),
                    sensorData = buildDefaultSensorDataInstance(),
                    isTrackingActive = isTrackingActive,
                    hasLocationPermission = !isTrackingActive,
                )
            assertEquals(isTrackingActive, telemetryState.isTrackingActive)
            assertEquals(!isTrackingActive, telemetryState.hasLocationPermission)
        }
    }

    @Nested
    inner class TrackingStateCombinations {
        @Test
        @DisplayName("Debería crearse una instancia de TelemetryState con tracking activo y permiso de ubicación")
        fun shouldCreateATelemetryStateInstanceWithTrackingActiveAndPermission() {
            val telemetryState =
                TelemetryState(
                    locationData = buildDefaultLocationDataInstance(),
                    sensorData = buildDefaultSensorDataInstance(),
                    isTrackingActive = true,
                    hasLocationPermission = true,
                )
            assertEquals(true, telemetryState.isTrackingActive)
            assertEquals(true, telemetryState.hasLocationPermission)
        }

        @Test
        @DisplayName("Debería crearse una instancia de TelemetryState con tracking activo sin permiso de ubicación")
        fun shouldCreateATelemetryStateInstanceWithTrackingActiveWithoutPermission() {
            val telemetryState =
                TelemetryState(
                    locationData = buildDefaultLocationDataInstance(),
                    sensorData = buildDefaultSensorDataInstance(),
                    isTrackingActive = true,
                    hasLocationPermission = false,
                )
            assertEquals(true, telemetryState.isTrackingActive)
            assertEquals(false, telemetryState.hasLocationPermission)
        }

        @Test
        @DisplayName("Debería crearse una instancia de TelemetryState sin tracking activo con permiso de ubicación")
        fun shouldCreateATelemetryStateInstanceWithoutTrackingActiveWithPermission() {
            val telemetryState =
                TelemetryState(
                    locationData = buildDefaultLocationDataInstance(),
                    sensorData = buildDefaultSensorDataInstance(),
                    isTrackingActive = false,
                    hasLocationPermission = true,
                )
            assertEquals(false, telemetryState.isTrackingActive)
            assertEquals(true, telemetryState.hasLocationPermission)
        }

        @Test
        @DisplayName("Debería crearse una instancia de TelemetryState sin tracking activo sin permiso de ubicación")
        fun shouldCreateATelemetryStateInstanceWithoutTrackingActiveAndWithoutPermission() {
            val telemetryState =
                TelemetryState(
                    locationData = buildDefaultLocationDataInstance(),
                    sensorData = buildDefaultSensorDataInstance(),
                    isTrackingActive = false,
                    hasLocationPermission = false,
                )
            assertEquals(false, telemetryState.isTrackingActive)
            assertEquals(false, telemetryState.hasLocationPermission)
        }
    }

    @Nested
    inner class CompositionWithNestedObjects {
        @Test
        @DisplayName("Debería crearse una instancia de TelemetryState con LocationData y SensorData válidos")
        fun shouldCreateATelemetryStateInstanceWithValidNestedObjects() {
            assertDoesNotThrow {
                TelemetryState(
                    locationData = buildDefaultLocationDataInstance(),
                    sensorData = buildDefaultSensorDataInstance(),
                    isTrackingActive = false,
                    hasLocationPermission = false,
                )
            }
        }

        @Test
        @DisplayName("Debería crearse una instancia de TelemetryState con LocationData y SensorData en sus estados típicos")
        fun shouldCreateATelemetryStateInstanceWithTypicalNestedObjects() {
            assertDoesNotThrow {
                TelemetryState(
                    locationData =
                        LocationData(
                            speed = Speed(60F),
                            latitude = 34.6037,
                            longitude = -58.3816,
                            isGpsAvailable = true,
                        ),
                    sensorData =
                        SensorData(
                            pitchDegree = PitchDegree(0F),
                            rollDegree = RollDegree(0F),
                            headingDegree = HeadingDegree(0F),
                        ),
                    isTrackingActive = false,
                    hasLocationPermission = true,
                )
            }
        }
    }

    @Nested
    inner class Immutability {
        @Test
        @DisplayName("Una instancia de TelemetryState debe ser inmutable después de su creación")
        fun shouldNotBeMutableAfterCreation() {
            val telemetryState =
                TelemetryState(
                    locationData = buildDefaultLocationDataInstance(),
                    sensorData = buildDefaultSensorDataInstance(),
                    isTrackingActive = false,
                    hasLocationPermission = false,
                )
            val copy = telemetryState.copy(isTrackingActive = true)
            assertEquals(false, telemetryState.isTrackingActive)
            assertEquals(true, copy.isTrackingActive)
        }
    }
}
