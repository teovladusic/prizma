package com.prizma_distribucija.prizma.feature_track_location.domain

import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.flow.StateFlow

interface LocationTracker {

    val isTrackingStateFlow: StateFlow<Boolean>

    val locations: StateFlow<List<Location>>

    fun startTracking(fusedLocationProviderClient: FusedLocationProviderClient)

    fun stopTracking()
}