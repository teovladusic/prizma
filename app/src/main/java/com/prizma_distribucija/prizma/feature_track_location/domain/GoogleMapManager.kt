package com.prizma_distribucija.prizma.feature_track_location.domain

import android.content.Context
import android.location.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

interface GoogleMapManager {

    fun onNewPathPoints(map: GoogleMap, pathPoints: List<LatLng>)

    fun setStyle(map: GoogleMap, context: Context)

    fun drawPolyLineBetweenAllPathPoints(map: GoogleMap, pathPoints: List<LatLng>)
}