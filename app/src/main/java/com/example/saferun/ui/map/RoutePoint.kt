package com.example.saferun.ui.map

import androidx.compose.runtime.Immutable

/** Lightweight geographic point used by the UI layer (no osmdroid types leak out). */
@Immutable
data class RoutePoint(
    val latitude: Double,
    val longitude: Double,
)
