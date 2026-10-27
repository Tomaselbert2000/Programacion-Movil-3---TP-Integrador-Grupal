package com.example.saferun.data.repository

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.saferun.data.repository.SensorIndex.AZIMUTH
import com.example.saferun.data.repository.SensorIndex.PITCH
import com.example.saferun.data.repository.SensorIndex.ROLL
import com.example.saferun.domain.model.HeadingDegree
import com.example.saferun.domain.model.PitchDegree
import com.example.saferun.domain.model.RollDegree
import com.example.saferun.domain.model.SensorData
import com.example.saferun.domain.repository.SensorTracker
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class SensorTrackerImpl
    @Inject
    constructor(
        private val sensorManager: SensorManager,
    ) : SensorTracker {
        override fun getSensorDataFlow(): Flow<SensorData> =
            callbackFlow {
                if (sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null) throw IllegalStateException()

                val listener =
                    object : SensorEventListener {
                        override fun onAccuracyChanged(
                            sensor: Sensor?,
                            accuracy: Int,
                        ) {
                        }

                        override fun onSensorChanged(event: SensorEvent?) {
                            event.let { sensorData ->

                                if (sensorData != null) {
                                    val sensorRead = mapSensorEventToSensorDataInstance(event)

                                    if (sensorRead != null) {
                                        trySend(sensorRead)
                                    }
                                }
                            }
                        }
                    }

                sensorManager.registerListener(
                    listener,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                    @Suppress("ktlint:standard:max-line-length")
                    SensorManager.SENSOR_DELAY_NORMAL,
                )

                awaitClose { sensorManager.unregisterListener(listener) }
            }

        private fun mapSensorEventToSensorDataInstance(sensorEvent: SensorEvent?): SensorData? {
            if (sensorEvent != null) {
                val azimuth = sensorEvent.values[AZIMUTH]
                val pitch = sensorEvent.values[PITCH]
                val roll = sensorEvent.values[ROLL]

                val azimuthToDegrees = convertToDegrees(azimuth)
                val pithToDegrees = convertToDegrees(pitch)
                val rollToDegrees = convertToDegrees(roll)

                return SensorData(
                    pitchDegree = PitchDegree(pithToDegrees.toFloat()),
                    rollDegree = RollDegree(rollToDegrees.toFloat()),
                    headingDegree = HeadingDegree(azimuthToDegrees.toFloat()),
                )
            }

            return null
        }

        private fun convertToDegrees(value: Float): Double = Math.toDegrees(value.toDouble())
    }

object SensorIndex {
    const val AZIMUTH = 0
    const val PITCH = 1
    const val ROLL = 2
}
