package com.prizma_distribucija.prizma.feature_track_location.domain.fakes

import android.content.Context
import android.location.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.prizma_distribucija.prizma.feature_track_location.domain.GoogleMapManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GoogleMapManagerFakeImpl : GoogleMapManager {

    companion object {
        var isOnNewPathPointsCalled = false
        var isSetStyleCalled = false
        var hasZoomedOut = false
        var isScreenshotTaken = false
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

    override val isReadyToScreenshot: StateFlow<Boolean>
        get() = MutableStateFlow(false).asStateFlow()

    override fun zoomOutToSeeEveryPathPoint(
        map: GoogleMap,
        latLngBounds: LatLngBounds,
        width: Int,
        height: Int,
        padding: Int
    ) {
        hasZoomedOut = true
    }

    override fun onScreenshotTaken() {
        isScreenshotTaken = true
    }
}