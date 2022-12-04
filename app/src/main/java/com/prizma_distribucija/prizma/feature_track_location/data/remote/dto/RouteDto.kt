package com.prizma_distribucija.prizma.feature_track_location.data.remote.dto

import com.google.firebase.firestore.GeoPoint

data class RouteDto(
    val avgSpeed: String,
    val distanceTravelled: String,
    val month: Int,
    val pathPointIds: List<String>,
    val timeFinished: String,
    val timeStarted: String,
    val userId: String,
    val year: Int,
    val day: Int
)