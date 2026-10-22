package com.example.saferun.domain.model

import com.example.saferun.domain.factory.TestDataFactory.Companion.buildDefaultSensorDataInstance
import com.example.saferun.domain.factory.TestDataFactory.Companion.generateRandomHeading
import com.example.saferun.domain.factory.TestDataFactory.Companion.generateRandomPitch
import com.example.saferun.domain.factory.TestDataFactory.Companion.generateRandomRoll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class SensorDataTest {
    @Nested
    inner class BasicConstructor {
        @Test
        @DisplayName("Debería crearse una instancia de SensorData con datos correctos")
        fun shouldCreateASensorDataInstanceWithValidData() {
            assertDoesNotThrow {
                buildDefaultSensorDataInstance()
            }
        }
    }

    @Nested
    inner class PitchValidation {
        @ParameterizedTest
        @DisplayName("No debería poder crear una instancia de SensorData con un pitch fuera del rango válido")
        @ValueSource(floats = [-95F, 95F])
        fun shouldNotCreateASensorDataInstanceWithInvalidPitch(pitch: Float) {
            assertThrows<IllegalArgumentException> {
                buildDefaultSensorDataInstance().withPitch(PitchDegree(pitch))
            }
        }
    }

    @Nested
    inner class RollValidation {
        @ParameterizedTest
        @DisplayName("No debería poder crear una instancia de SensorData con un roll fuera del rango válido")
        @ValueSource(floats = [-185.6F, 185.6F])
        fun shouldNotCreateASensorDataInstanceWithInvalidRoll(roll: Float) {
            assertThrows<IllegalArgumentException> {
                buildDefaultSensorDataInstance().withRoll(RollDegree(roll))
            }
        }
    }

    @Nested
    inner class HeadingValidation {
        @ParameterizedTest
        @DisplayName("No debería poder crear una instancia de SensorData con un heading fuera del rango válido")
        @ValueSource(floats = [-220.9F, 181.0F])
        fun shouldNotCreateASensorDataInstanceWithInvalidHeading(heading: Float) {
            assertThrows<IllegalArgumentException> {
                buildDefaultSensorDataInstance().withHeading(HeadingDegree(heading))
            }
        }
    }

    @Nested
    inner class PitchBoundaries {
        @ParameterizedTest
        @DisplayName("Debería crearse una instancia de SensorData con pitch en límites válidos")
        @ValueSource(floats = [-90F, 90F])
        fun shouldCreateASensorDataInstanceWithValidPitchBoundary(pitch: Float) {
            assertDoesNotThrow {
                buildDefaultSensorDataInstance().withPitch(PitchDegree(pitch))
            }
        }
    }

    @Nested
    inner class RollBoundaries {
        @ParameterizedTest
        @DisplayName("Debería crearse una instancia de SensorData con roll en límites válidos")
        @ValueSource(floats = [-180F, 180F])
        fun shouldCreateASensorDataInstanceWithValidRollBoundary(roll: Float) {
            assertDoesNotThrow {
                buildDefaultSensorDataInstance().withRoll(RollDegree(roll))
            }
        }
    }

    @Nested
    inner class HeadingBoundaries {
        @ParameterizedTest
        @DisplayName("Debería crearse una instancia de SensorData con heading en límites válidos")
        @ValueSource(floats = [-180F, 180F])
        fun shouldCreateASensorDataInstanceWithValidHeadingBoundary(heading: Float) {
            assertDoesNotThrow {
                buildDefaultSensorDataInstance().withHeading(HeadingDegree(heading))
            }
        }
    }

    @Nested
    inner class HeadingEdgeCases {
        @ParameterizedTest
        @DisplayName("Debería crearse una instancia de SensorData con heading cardinal o intermedio válido")
        @ValueSource(floats = [0F, 90F, -90F, 45F, -45F, -0.01F, 0.01F])
        fun shouldCreateASensorDataInstanceWithValidHeadingCardinalOrIntermediate(heading: Float) {
            assertDoesNotThrow {
                buildDefaultSensorDataInstance().withHeading(HeadingDegree(heading))
            }
        }
    }

    @Nested
    inner class CommonSensorValues {
        @Test
        @DisplayName("Debería crearse una instancia de SensorData con dispositivo horizontal")
        fun shouldCreateASensorDataInstanceWithHorizontalDevice() {
            assertDoesNotThrow {
                buildDefaultSensorDataInstance()
                    .withPitch(PitchDegree(0F))
                    .withRoll(RollDegree(0F))
                    .withHeading(HeadingDegree(0F))
            }
        }

        @ParameterizedTest
        @DisplayName("Debería crearse una instancia de SensorData con orientación cardinal válida")
        @ValueSource(floats = [0F, 90F, 180F, -90F])
        fun shouldCreateASensorDataInstanceWithCardinalOrientation(heading: Float) {
            assertDoesNotThrow {
                buildDefaultSensorDataInstance()
                    .withPitch(PitchDegree(0F))
                    .withRoll(RollDegree(0F))
                    .withHeading(HeadingDegree(heading))
            }
        }

        @ParameterizedTest
        @DisplayName("Debería crearse una instancia de SensorData con inclinación de pitch común")
        @ValueSource(floats = [45F, -45F, 30F, -30F])
        fun shouldCreateASensorDataInstanceWithCommonPitchTilt(pitch: Float) {
            assertDoesNotThrow {
                buildDefaultSensorDataInstance()
                    .withPitch(PitchDegree(pitch))
                    .withRoll(RollDegree(0F))
                    .withHeading(HeadingDegree(0F))
            }
        }

        @ParameterizedTest
        @DisplayName("Debería crearse una instancia de SensorData con inclinación de roll común")
        @ValueSource(floats = [180F, 90F, -90F])
        fun shouldCreateASensorDataInstanceWithCommonRollTilt(roll: Float) {
            assertDoesNotThrow {
                buildDefaultSensorDataInstance()
                    .withPitch(PitchDegree(0F))
                    .withRoll(RollDegree(roll))
                    .withHeading(HeadingDegree(0F))
            }
        }
    }

    @Nested
    inner class RealisticSensorValues {
        @Test
        @DisplayName("Debería crearse una instancia de SensorData con valores de pitch aleatorios válidos")
        fun shouldCreateASensorDataInstanceWithRandomValidPitchValues() {
            assertDoesNotThrow {
                buildDefaultSensorDataInstance().withPitch(generateRandomPitch())
            }
        }

        @Test
        @DisplayName("Debería crearse una instancia de SensorData con valores de roll aleatorios válidos")
        fun shouldCreateASensorDataInstanceWithRandomValidRollValues() {
            assertDoesNotThrow {
                buildDefaultSensorDataInstance().withRoll(generateRandomRoll())
            }
        }

        @Test
        @DisplayName("Debería crearse una instancia de SensorData con valores de heading aleatorios válidos")
        fun shouldCreateASensorDataInstanceWithRandomValidHeadingValues() {
            assertDoesNotThrow {
                buildDefaultSensorDataInstance().withHeading(generateRandomHeading())
            }
        }
    }
}
