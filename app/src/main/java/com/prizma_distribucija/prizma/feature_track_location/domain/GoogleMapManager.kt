package com.prizma_distribucija.prizma.feature_track_location.domain

import android.content.Context
import android.location.Location
import com.google.android.gms.maps.GoogleMap

interface GoogleMapManager {

    fun onNewPathPoints(map: GoogleMap, locations: List<Location>)

    fun setStyle(map: GoogleMap, context: Context)

    fun drawPolyLineBetweenAllPathPoints(map: GoogleMap, locations: List<Location>)
}