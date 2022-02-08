package com.prizma_distribucija.prizma.feature_track_location.domain.fakes

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.prizma_distribucija.prizma.feature_track_location.domain.GoogleMapManager

class GoogleMapMangerFakeImpl : GoogleMapManager {

    companion object {
        var isOnNewPathPointsCalled = false
        var isSetStyleCalled = false
    }

    override fun setStyle(map: GoogleMap, context: Context) {
        isSetStyleCalled = true
        return
    }

    override fun drawPolyLineBetweenAllPathPoints(map: GoogleMap, pathPoints: List<LatLng>) {
        TODO("Not yet implemented")
    }

    override fun onNewPathPoints(map: GoogleMap, pathPoints: List<LatLng>) {
        isOnNewPathPointsCalled = true
        return
    }
}