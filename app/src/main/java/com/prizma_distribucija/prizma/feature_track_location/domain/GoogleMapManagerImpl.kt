package com.prizma_distribucija.prizma.feature_track_location.domain

import android.content.Context
import android.location.Location
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.SphericalUtil
import com.prizma_distribucija.prizma.R
import com.prizma_distribucija.prizma.core.util.Constants
import com.prizma_distribucija.prizma.core.util.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class GoogleMapManagerImpl(
    private val dispatchers: DispatcherProvider
) : GoogleMapManager {

    private val polylineOptions = PolylineOptions()
        .color(Constants.POLYLINE_COLOR)
        .width(Constants.POLYLINE_WIDTH)

    override fun onNewPathPoints(map: GoogleMap, pathPoints: List<LatLng>) {
        if (pathPoints.isEmpty()) return
        drawPolyLineBetweenCurrentAndPreviousLocation(map, pathPoints.last())
        if (pathPoints.size < 2) return
        val preLastLatLng = pathPoints.dropLast(1).last()
        moveCameraAndZoomIfNeeded(map, pathPoints.last(), preLastLatLng)
    }

    private fun drawPolyLineBetweenCurrentAndPreviousLocation(map: GoogleMap, latLng: LatLng) {
        CoroutineScope(dispatchers.main).launch {
            map.addPolyline(
                polylineOptions
                    .add(latLng)
            )
        }
    }

    override fun drawPolyLineBetweenAllPathPoints(map: GoogleMap, pathPoints: List<LatLng>) {
        CoroutineScope(dispatchers.main).launch {
            map.addPolyline(
                polylineOptions
                    .addAll(pathPoints)
            )
        }
    }

    override fun setStyle(map: GoogleMap, context: Context) {
        map.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                context,
                R.raw.google_maps_style
            )
        )
    }

    private fun moveCameraAndZoomIfNeeded(map: GoogleMap, latLng: LatLng, preLastLatLng: LatLng) {
        val zoom =
            if (map.cameraPosition.zoom < 12f) Constants.DEFAULT_MAP_ZOOM
            else map.cameraPosition.zoom

        val firstLocation = Location("")
        firstLocation.longitude = preLastLatLng.longitude
        firstLocation.latitude = preLastLatLng.latitude

        val secondLocation = Location("")
        secondLocation.longitude = latLng.longitude
        secondLocation.latitude = latLng.latitude

        val rotation = firstLocation.bearingTo(secondLocation)


        val position =
            CameraPosition.Builder()
                .bearing(rotation)
                .target(latLng)
                .tilt(60f)
                .zoom(zoom)

        map.animateCamera(CameraUpdateFactory.newCameraPosition(position.build()))
    }
}