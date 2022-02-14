package com.prizma_distribucija.prizma.feature_track_location.domain

import android.content.Context
import android.location.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.flow.StateFlow

interface GoogleMapManager {

    val isReadyToScreenshot: StateFlow<Boolean>

    fun onScreenshotTaken()

    fun onNewPathPoints(map: GoogleMap, locations: List<Location>)

    fun setStyle(map: GoogleMap, context: Context)

    fun drawPolyLineBetweenAllPathPoints(map: GoogleMap, locations: List<Location>)

    fun zoomOutToSeeEveryPathPoint(map: GoogleMap, latLngBounds: LatLngBounds, width: Int, height: Int, padding: Int)
}