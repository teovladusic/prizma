package com.prizma_distribucija.prizma.feature_track_location.domain

import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.prizma_distribucija.prizma.feature_track_location.domain.model.MarkerPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface LocationTracker {

    val isTrackingStateFlow: StateFlow<Boolean>

    val markerPoints: StateFlow<List<MarkerPoint>>

    val locations: StateFlow<List<Location>>

    fun startTracking(fusedLocationProviderClient: FusedLocationProviderClient)

    fun stopTracking()
}