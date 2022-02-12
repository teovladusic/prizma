package com.prizma_distribucija.prizma.feature_track_location.domain

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.prizma_distribucija.prizma.R
import com.prizma_distribucija.prizma.core.util.Constants
import com.prizma_distribucija.prizma.core.util.DispatcherProvider
import com.prizma_distribucija.prizma.feature_track_location.presentation.track_location.BearingCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class GoogleMapManagerImpl(
    private val dispatchers: DispatcherProvider
) : GoogleMapManager {

    private val polylineOptions = PolylineOptions()
        .color(Constants.POLYLINE_COLOR)
        .width(Constants.POLYLINE_WIDTH)

    private var map: GoogleMap? = null
    private var latestLatLng: LatLng? = null

    override fun onNewPathPoints(map: GoogleMap, locations: List<Location>) {
        this.map = map
        if (locations.isEmpty()) return
        this.latestLatLng = LatLng(locations.last().latitude, locations.last().longitude)
        drawPolyLineBetweenCurrentAndPreviousLocation(map, latestLatLng!!)
        if (locations.size < 2) return
        moveCameraAndZoomIfNeeded(map, locations.last())
    }

    private fun drawPolyLineBetweenCurrentAndPreviousLocation(map: GoogleMap, latLng: LatLng) {
        CoroutineScope(dispatchers.main).launch {
            map.addPolyline(
                polylineOptions
                    .add(latLng)
            )
        }
    }

    override fun drawPolyLineBetweenAllPathPoints(map: GoogleMap, locations: List<Location>) {
        CoroutineScope(dispatchers.main).launch {
            val pathPoints = locations.map { LatLng(it.latitude, it.longitude) }
            map.addPolyline(
                polylineOptions
                    .addAll(pathPoints)
            )
        }
    }

    @SuppressLint("MissingPermission")
    override fun setStyle(map: GoogleMap, context: Context) {
        map.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                context,
                R.raw.google_maps_style
            )
        )
        map.isMyLocationEnabled = true
        map.isIndoorEnabled = true
        map.isBuildingsEnabled = true
    }


    private fun moveCameraAndZoomIfNeeded(
        map: GoogleMap,
        location: Location,
    ) {
        val zoom =
            if (map.cameraPosition.zoom < 12f) Constants.DEFAULT_MAP_ZOOM
            else map.cameraPosition.zoom

        val position =
            CameraPosition.Builder()
                .target(LatLng(location.latitude, location.longitude))
                .tilt(60f)
                .bearing(BearingCalculator.currentBearing)
                .zoom(zoom)

        map.animateCamera(CameraUpdateFactory.newCameraPosition(position.build()))
    }
}