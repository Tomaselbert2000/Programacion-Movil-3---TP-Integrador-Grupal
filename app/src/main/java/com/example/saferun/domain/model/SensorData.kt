package com.example.saferun.domain.model

import com.example.saferun.domain.model.SensorDataConstraints.INVALID_HEADING
import com.example.saferun.domain.model.SensorDataConstraints.INVALID_PITCH
import com.example.saferun.domain.model.SensorDataConstraints.INVALID_ROLL
import com.example.saferun.domain.model.SensorDataConstraints.MAX_HEADING
import com.example.saferun.domain.model.SensorDataConstraints.MAX_PITCH
import com.example.saferun.domain.model.SensorDataConstraints.MAX_ROLL
import com.example.saferun.domain.model.SensorDataConstraints.MIN_HEADING
import com.example.saferun.domain.model.SensorDataConstraints.MIN_PITCH
import com.example.saferun.domain.model.SensorDataConstraints.MIN_ROLL

@JvmInline
value class PitchDegree(
    val value: Float,
)

@JvmInline
value class RollDegree(
    val value: Float,
)

@JvmInline
value class HeadingDegree(
    val value: Float,
)

data class SensorData(
    val pitchDegree: PitchDegree,
    val rollDegree: RollDegree,
    val headingDegree: HeadingDegree,
) {
    init {
        require(pitchDegree.value in MIN_PITCH..MAX_PITCH) { INVALID_PITCH }
        require(rollDegree.value in MIN_ROLL..MAX_ROLL) { INVALID_ROLL }
        require(headingDegree.value in MIN_HEADING..MAX_HEADING) { INVALID_HEADING }
    }

    fun withPitch(newPitch: PitchDegree): SensorData = this.copy(pitchDegree = newPitch)

    fun withRoll(newRoll: RollDegree): SensorData = this.copy(rollDegree = newRoll)

    fun withHeading(newHeading: HeadingDegree): SensorData = this.copy(headingDegree = newHeading)
}

object SensorDataConstraints {
    const val MIN_PITCH = -90F
    const val MAX_PITCH = 90F
    const val MIN_ROLL = -180F
    const val MAX_ROLL = 180F
    const val MIN_HEADING = -180F
    const val MAX_HEADING = 180F
    const val INVALID_PITCH =
        "El valor de inclinación o cabeceo del dispositivo no puede ser menor a -90 grados ni mayor a 90 grados"
    const val INVALID_ROLL =
        "El valor de ladeo del dispositivo no puede ser menor a -180 grados ni mayor a 180 grados"
    const val INVALID_HEADING =
        "El valor de orientación del dispositivo no puede ser menor a -180 grados ni mayor a 180 grados"
}
