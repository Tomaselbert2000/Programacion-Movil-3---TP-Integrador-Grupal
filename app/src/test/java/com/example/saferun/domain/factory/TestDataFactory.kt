package com.example.saferun.domain.factory

import com.example.saferun.domain.model.HeadingDegree
import com.example.saferun.domain.model.LocationData
import com.example.saferun.domain.model.PitchDegree
import com.example.saferun.domain.model.RollDegree
import com.example.saferun.domain.model.SensorData
import com.example.saferun.domain.model.Speed

class TestDataFactory {
    companion object {
        @JvmStatic
        fun buildDefaultLocationDataInstance(): LocationData =
            LocationData(
                speed = Speed(60F),
                latitude = 20.2,
                longitude = 120.9,
                isGpsAvailable = true,
                timestamp = System.currentTimeMillis(),
            )

        @JvmStatic
        fun buildDefaultSensorDataInstance(): SensorData =
            SensorData(
                pitchDegree = PitchDegree(FactoryConstants.PITCH_VALUE),
                rollDegree = RollDegree(FactoryConstants.ROLL_VALUE),
                headingDegree = HeadingDegree(FactoryConstants.HEADING_VALUE),
            )

        @JvmStatic
        fun generateRandomPitch(): PitchDegree = PitchDegree((Math.random() * 180 - 90).toFloat())

        @JvmStatic
        fun generateRandomRoll(): RollDegree = RollDegree((Math.random() * 360 - 180).toFloat())

        @JvmStatic
        fun generateRandomHeading(): HeadingDegree = HeadingDegree((Math.random() * 360 - 180).toFloat())
    }
}

object FactoryConstants {
    const val PITCH_VALUE = 90F
    const val ROLL_VALUE = -180F
    const val HEADING_VALUE = 180F
}
