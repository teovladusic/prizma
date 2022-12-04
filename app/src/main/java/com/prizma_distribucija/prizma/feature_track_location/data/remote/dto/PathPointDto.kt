package com.prizma_distribucija.prizma.feature_track_location.data.remote.dto

import com.google.firebase.firestore.GeoPoint

data class PathPointDto(
    val id: String,
    val points: List<GeoPoint>,
    val index: Int
)