package com.example.saferun.data.local

import com.example.saferun.data.local.LocationDataConstraints.INVALID_LATITUDE
import com.example.saferun.data.local.LocationDataConstraints.INVALID_LONGITUDE
import com.example.saferun.data.local.LocationDataConstraints.MAX_LATITUDE
import com.example.saferun.data.local.LocationDataConstraints.MAX_LONGITUDE
import com.example.saferun.data.local.LocationDataConstraints.MIN_LATITUDE
import com.example.saferun.data.local.LocationDataConstraints.MIN_LONGITUDE
import com.example.saferun.data.local.LocationDataConstraints.MIN_SPEED
import com.example.saferun.data.local.LocationDataConstraints.NEGATIVE_SPEED

@JvmInline
value class Speed(
    val value: Float,
)

@Suppress("ktlint:standard:max-line-length")
data class LocationData(
    val speed: Speed,
    val latitude: Double,
    val longitude: Double,
    val isGpsAvailable: Boolean,
) {
    init {

        require(speed.value >= MIN_SPEED) { NEGATIVE_SPEED }
        require(latitude in MIN_LATITUDE..MAX_LATITUDE) { INVALID_LATITUDE }
        require(longitude in MIN_LONGITUDE..MAX_LONGITUDE) { INVALID_LONGITUDE }
    }
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
}
