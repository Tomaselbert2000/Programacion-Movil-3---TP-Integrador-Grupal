package com.example.saferun.domain.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.saferun.domain.model.LocationDataConstraints.INVALID_TIMESTAMP

@JvmInline
value class Speed(
    val value: Float,
)

@RequiresApi(Build.VERSION_CODES.S)
@Suppress("ktlint:standard:max-line-length")
data class LocationData(
    val speed: Speed,
    val latitude: Double,
    val longitude: Double,
    val isGpsAvailable: Boolean,
    val timestamp: Long,
) {
    init {

        require(speed.value >= LocationDataConstraints.MIN_SPEED) { LocationDataConstraints.NEGATIVE_SPEED }
        require(
            latitude in LocationDataConstraints.MIN_LATITUDE..LocationDataConstraints.MAX_LATITUDE,
        ) { LocationDataConstraints.INVALID_LATITUDE }
        require(longitude in LocationDataConstraints.MIN_LONGITUDE..LocationDataConstraints.MAX_LONGITUDE) {
            LocationDataConstraints.INVALID_LONGITUDE
        }
        require(timestamp > 0L) { INVALID_TIMESTAMP }
    }

    fun withSpeed(newSpeed: Speed): LocationData = this.copy(speed = newSpeed)

    fun withLatitude(newLatitude: Double): LocationData = this.copy(latitude = newLatitude)

    fun withLongitude(newLongitude: Double): LocationData = this.copy(longitude = newLongitude)

    fun withTimestamp(newTimestamp: Long): LocationData = this.copy(timestamp = newTimestamp)
}

object LocationDataConstraints {
    const val MIN_SPEED = 0F
    const val MAX_LATITUDE = 90.0
    const val MIN_LATITUDE = -90.0
    const val MAX_LONGITUDE = 180.0
    const val MIN_LONGITUDE = -180.0
    const val NEGATIVE_SPEED: String = "La velocidad no puede ser negativa"
    const val INVALID_LATITUDE =
        "El valor de latitud no puede ser menor a -90 grados o mayor a 90 grados"
    const val INVALID_LONGITUDE =
        "El valor de longitud no puede ser menor a -180 grados o mayor a 180 grados"
    const val INVALID_TIMESTAMP = "El valor del timestamp no puede ser igual o menor a 0L"
}
