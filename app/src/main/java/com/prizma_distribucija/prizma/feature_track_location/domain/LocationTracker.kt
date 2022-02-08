package com.prizma_distribucija.prizma.feature_track_location.domain

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface LocationTracker {

    val isTrackingStateFlow: StateFlow<Boolean>

    val pathPoints: StateFlow<List<LatLng>>

    fun startTracking(fusedLocationProviderClient: FusedLocationProviderClient)

    fun stopTracking()
}