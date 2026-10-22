package com.example.saferun.domain.model

import com.example.saferun.domain.factory.TestDataFactory.Companion.buildDefaultLocationDataInstance
import com.example.saferun.domain.model.LocationDataConstraints.INVALID_LATITUDE
import com.example.saferun.domain.model.LocationDataConstraints.INVALID_LONGITUDE
import com.example.saferun.domain.model.LocationDataConstraints.INVALID_TIMESTAMP
import com.example.saferun.domain.model.LocationDataConstraints.NEGATIVE_SPEED
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class LocationDataTest {
    @Nested
    inner class BasicConstructor {
        @Test
        @DisplayName("Debería crearse una instancia de LocationData con datos correctos")
        fun shouldCreateALocationDataInstanceWithValidData() {
            assertDoesNotThrow {
                buildDefaultLocationDataInstance()
            }
        }
    }

    @Nested
    inner class LatitudeAndLongitudeRange {
        @Test
        @DisplayName("No debería poder crear una instancia de LocationData con una latitud menor a -90 grados")
        fun shouldNotCreateALocationDataInstanceWithLatitudeLowerThanMinus90() {
            val exception =
                assertThrows<IllegalArgumentException> {
                    buildDefaultLocationDataInstance().withLatitude(-91.0)
                }

            assertEquals(exception.message, INVALID_LATITUDE)
        }

        @Test
        @DisplayName("No debería poder crear una instancia de LocationData con una latitud mayor a 90 grados")
        fun shouldNotCreateALocationDataInstanceWithLatitudeHigherThan90() {
            val exception =
                assertThrows<IllegalArgumentException> {
                    buildDefaultLocationDataInstance().withLatitude(125.5)
                }

            assertEquals(exception.message, INVALID_LATITUDE)
        }

        @Test
        @DisplayName("No debería poder crear una instancia de LocationData con una longitud menor a -180 grados")
        fun shouldNotCreateALocationDataInstanceWithLongitudeLowerThanMinus180() {
            val exception =
                assertThrows<IllegalArgumentException> {
                    buildDefaultLocationDataInstance().withLongitude(-189.6)
                }

            assertEquals(exception.message, INVALID_LONGITUDE)
        }

        @Test
        @DisplayName("No debería poder crear una instancia de LocationData con una longitud mayor a 180 grados")
        fun shouldNotCreateALocationDataInstanceWithLongitudeHigherThan180() {
            val exception =
                assertThrows<IllegalArgumentException> {
                    buildDefaultLocationDataInstance().withLongitude(189.6)
                }

            assertEquals(exception.message, INVALID_LONGITUDE)
        }

        @Test
        @DisplayName("No debería arrojar ninguna excepción si la latitud es exactamente 0")
        fun shouldNotThrowAnythingIfLatitudeIsZero() {
            assertDoesNotThrow {
                buildDefaultLocationDataInstance().withLatitude(0.0)
            }
        }

        @Test
        @DisplayName("No debería arrojar ninguna excepción si la latitud es exactamente 90")
        fun shouldNotThrowAnythingIfLatitudeIsExactly90() {
            assertDoesNotThrow {
                buildDefaultLocationDataInstance().withLatitude(90.0)
            }
        }

        @Test
        @DisplayName("No debería arrojar ninguna excepción si la latitud es exactamente -90")
        fun shouldNotThrowAnythingIfLatitudeIsExactlyMinus90() {
            assertDoesNotThrow {
                buildDefaultLocationDataInstance().withLatitude(-90.0)
            }
        }

        @Test
        @DisplayName("No debería arrojar ninguna excepción si la longitud es exactamente 0")
        fun shouldNotThrowAnythingIfLongitudeIsZero() {
            assertDoesNotThrow {
                buildDefaultLocationDataInstance().withLongitude(0.0)
            }
        }

        @Test
        @DisplayName("No debería arrojar ninguna excepción si la longitud es exactamente 180")
        fun shouldNotThrowAnythingIfLongitudeIsExactly180() {
            assertDoesNotThrow {
                buildDefaultLocationDataInstance().withLongitude(180.0)
            }
        }

        @Test
        @DisplayName("No debería arrojar ninguna excepción si la longitud es exactamente -180")
        fun shouldNotThrowAnythingIfLongitudeIsExactlyMinus180() {
            assertDoesNotThrow {
                buildDefaultLocationDataInstance().withLongitude(-180.0)
            }
        }

        @Test
        @DisplayName("No debería arrojar ninguna excepción si tanto latitud y longitud son exactamente cero")
        fun shouldNotThrowAnythingIfBothLatitudeAndLongitudeAreZero() {
            assertDoesNotThrow {
                buildDefaultLocationDataInstance().withLongitude(0.0).withLatitude(0.0)
            }
        }
    }

    @Nested
    inner class SpeedRanges {
        @Test
        @DisplayName("No debería crearse una instancia de LocationData con una velocidad negativa")
        fun shouldNotCreateALocationDataInstanceWithInvalidSpeed() {
            val exception =
                assertThrows<IllegalArgumentException> {
                    buildDefaultLocationDataInstance().withSpeed(Speed(-100F))
                }

            assertEquals(exception.message, NEGATIVE_SPEED)
        }

        @Test
        @DisplayName("No debería arrojar excepción si la velocidad actual es exactamente 0 kilómetros por hora")
        fun shouldNotThrowExceptionWhenSpeedIsZero() {
            assertDoesNotThrow {
                buildDefaultLocationDataInstance().withSpeed(Speed(0F))
            }
        }
    }

    @Nested
    inner class TimestampValidation {
        @Test
        @DisplayName("Debería crearse una instancia de LocationData con timestamp válido")
        fun shouldCreateALocationDataInstanceWithValidTimestamp() {
            assertDoesNotThrow { buildDefaultLocationDataInstance() }
        }

        @Test
        @DisplayName("No debería poder crear una instancia de LocationData con timestamp cero")
        fun shouldNotCreateALocationDataInstanceWithZeroTimestamp() {
            val exception =
                assertThrows<IllegalArgumentException> {
                    buildDefaultLocationDataInstance().withTimestamp(
                        0L,
                    )
                }

            assertEquals(exception.message, INVALID_TIMESTAMP)
        }

        @Test
        @DisplayName("No debería poder crear una instancia de LocationData con un timestamp menor a cero")
        fun shouldNotCreateALocationDataInstanceWithTimestampLowerThanZero() {
            val exception =
                assertThrows<IllegalArgumentException> {
                    buildDefaultLocationDataInstance().withTimestamp(
                        -9999L,
                    )
                }

            assertEquals(exception.message, INVALID_TIMESTAMP)
        }
    }
}
