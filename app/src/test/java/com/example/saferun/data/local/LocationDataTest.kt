package com.example.saferun.data.local

import com.example.saferun.data.local.LocationDataConstraints.INVALID_LATITUDE
import com.example.saferun.data.local.LocationDataConstraints.INVALID_LONGITUDE
import com.example.saferun.data.local.LocationDataConstraints.NEGATIVE_SPEED
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
                LocationData(
                    Speed(60F),
                    latitude = 20.2,
                    longitude = 120.9,
                    isGpsAvailable = true,
                )
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
                    LocationData(
                        Speed(60F),
                        latitude = -91.0,
                        longitude = 120.9,
                        isGpsAvailable = true,
                    )
                }

            assertEquals(exception.message, INVALID_LATITUDE)
        }

        @Test
        @DisplayName("No debería poder crear una instancia de LocationData con una latitud mayor a 90 grados")
        fun shouldNotCreateALocationDataInstanceWithLatitudeHigherThan90() {
            val exception =
                assertThrows<IllegalArgumentException> {
                    LocationData(
                        Speed(60F),
                        latitude = 125.5,
                        longitude = 120.9,
                        isGpsAvailable = true,
                    )
                }

            assertEquals(exception.message, INVALID_LATITUDE)
        }

        @Test
        @DisplayName("No debería poder crear una instancia de LocationData con una longitud menor a -180 grados")
        fun shouldNotCreateALocationDataInstanceWithLongitudeLowerThanMinus180() {
            val exception =
                assertThrows<IllegalArgumentException> {
                    LocationData(
                        Speed(60F),
                        latitude = 62.1,
                        longitude = -189.6,
                        isGpsAvailable = true,
                    )
                }

            assertEquals(exception.message, INVALID_LONGITUDE)
        }

        @Test
        @DisplayName("No debería poder crear una instancia de LocationData con una longitud mayor a 180 grados")
        fun shouldNotCreateALocationDataInstanceWithLongitudeHigherThan180() {
            val exception =
                assertThrows<IllegalArgumentException> {
                    LocationData(
                        Speed(60F),
                        latitude = 62.1,
                        longitude = 189.6,
                        isGpsAvailable = true,
                    )
                }

            assertEquals(exception.message, INVALID_LONGITUDE)
        }

        @Test
        @DisplayName("No debería arrojar ninguna excepción si la latitud es exactamente 0")
        fun shouldNotThrowAnythingIfLatitudeIsZero() {
            assertDoesNotThrow {
                LocationData(
                    Speed(60F),
                    latitude = 0.0,
                    longitude = 144.6,
                    isGpsAvailable = true,
                )
            }
        }

        @Test
        @DisplayName("No debería arrojar ninguna excepción si la latitud es exactamente 90")
        fun shouldNotThrowAnythingIfLatitudeIsExactly90() {
            assertDoesNotThrow {
                LocationData(
                    Speed(60F),
                    latitude = 90.0,
                    longitude = 144.6,
                    isGpsAvailable = true,
                )
            }
        }

        @Test
        @DisplayName("No debería arrojar ninguna excepción si la latitud es exactamente -90")
        fun shouldNotThrowAnythingIfLatitudeIsExactlyMinus90() {
            assertDoesNotThrow {
                LocationData(
                    Speed(60F),
                    latitude = -90.0,
                    longitude = 144.6,
                    isGpsAvailable = true,
                )
            }
        }

        @Test
        @DisplayName("No debería arrojar ninguna excepción si la longitud es exactamente 0")
        fun shouldNotThrowAnythingIfLongitudeIsZero() {
            assertDoesNotThrow {
                LocationData(
                    Speed(60F),
                    latitude = 52.78,
                    longitude = 0.0,
                    isGpsAvailable = true,
                )
            }
        }

        @Test
        @DisplayName("No debería arrojar ninguna excepción si la longitud es exactamente 180")
        fun shouldNotThrowAnythingIfLongitudeIsExactly180() {
            assertDoesNotThrow {
                LocationData(
                    Speed(60F),
                    latitude = 52.78,
                    longitude = 180.0,
                    isGpsAvailable = true,
                )
            }
        }

        @Test
        @DisplayName("No debería arrojar ninguna excepción si la longitud es exactamente -180")
        fun shouldNotThrowAnythingIfLongitudeIsExactlyMinus180() {
            assertDoesNotThrow {
                LocationData(
                    Speed(60F),
                    latitude = 52.78,
                    longitude = -180.0,
                    isGpsAvailable = true,
                )
            }
        }

        @Test
        @DisplayName("No debería arrojar ninguna excepción si tanto latitud y longitud son exactamente cero")
        fun shouldNotThrowAnythingIfBothLatitudeAndLongitudeAreZero() {
            assertDoesNotThrow {
                LocationData(
                    Speed(60F),
                    latitude = 0.0,
                    longitude = 0.0,
                    isGpsAvailable = true,
                )
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
                    LocationData(
                        Speed(-100F),
                        latitude = 20.2,
                        longitude = 120.9,
                        isGpsAvailable = true,
                    )
                }

            assertEquals(exception.message, NEGATIVE_SPEED)
        }

        @Test
        @DisplayName("No debería arrojar excepción si la velocidad actual es exactamente 0 kilómetros por hora")
        fun shouldNotThrowExceptionWhenSpeedIsZero() {
            assertDoesNotThrow {
                LocationData(
                    Speed(0F),
                    latitude = 20.2,
                    longitude = 120.9,
                    isGpsAvailable = true,
                )
            }
        }
    }
}
