package com.prizma_distribucija.prizma.feature_track_location.domain.model

import com.google.android.gms.maps.model.LatLng

data class MarkerPoint(
    val location: LatLng,
    val text: String
)