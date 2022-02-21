package com.prizma_distribucija.prizma.feature_track_location.domain.model

import com.google.android.gms.maps.model.LatLng

data class Route(
    val avgSpeed: String,
    val distanceTravelled: String,
    val month: Int,
    val pathPoints: List<LatLng>,
    val timeFinished: String,
    val timeStarted: String,
    val userId: String,
    val year: Int,
    val day: Int
)