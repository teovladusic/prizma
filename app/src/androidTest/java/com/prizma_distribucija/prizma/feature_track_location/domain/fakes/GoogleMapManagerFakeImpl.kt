package com.prizma_distribucija.prizma.feature_track_location.domain.fakes

import android.content.Context
import android.location.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.prizma_distribucija.prizma.feature_track_location.domain.GoogleMapManager
import com.prizma_distribucija.prizma.feature_track_location.domain.model.MarkerPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GoogleMapManagerFakeImpl : GoogleMapManager {

    companion object {
        var isOnNewPathPointsCalled = false
        var isSetStyleCalled = false
        var hasZoomedOut = false
    }

    override fun setStyle(map: GoogleMap, context: Context) {
        isSetStyleCalled = true
        return
    }

    override fun drawPolyLineBetweenAllPathPoints(map: GoogleMap, locations: List<Location>) {
        return
    }

    override fun onNewPathPoints(map: GoogleMap, locations: List<Location>) {
        isOnNewPathPointsCalled = true
        return
    }

    override fun drawMarkerPoints(
        map: GoogleMap,
        markerPoints: List<MarkerPoint>,
        context: Context
    ) {
        return
    }
}