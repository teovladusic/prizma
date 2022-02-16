package com.prizma_distribucija.prizma.feature_track_location.domain

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.location.Location
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.prizma_distribucija.prizma.R
import com.prizma_distribucija.prizma.core.util.Constants
import com.prizma_distribucija.prizma.core.util.DispatcherProvider
import com.prizma_distribucija.prizma.feature_track_location.domain.model.MarkerPoint
import com.prizma_distribucija.prizma.feature_track_location.presentation.track_location.BearingCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


class GoogleMapManagerImpl @Inject constructor(
    private val dispatchers: DispatcherProvider,
) : GoogleMapManager {

    private val polylineOptions = PolylineOptions()
        .color(Constants.POLYLINE_COLOR)
        .width(Constants.POLYLINE_WIDTH)

    private var map: GoogleMap? = null
    private var latestLatLng: LatLng? = null


    private val _isReadyToScreenshot = MutableStateFlow(false)
    override val isReadyToScreenshot: StateFlow<Boolean> = _isReadyToScreenshot.asStateFlow()

    override fun onScreenshotTaken() {
        CoroutineScope(dispatchers.default).launch {
            _isReadyToScreenshot.emit(false)
        }
    }

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

    override fun zoomOutToSeeEveryPathPoint(
        map: GoogleMap,
        latLngBounds: LatLngBounds,
        width: Int,
        height: Int,
        padding: Int
    ) {
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(
            latLngBounds,
            width,
            height,
            padding
        )

        map.moveCamera(cameraUpdate)
        CoroutineScope(dispatchers.default).launch {
            _isReadyToScreenshot.emit(true)
        }
    }

    private val drawnMarkers = emptyList<Marker>()

    override fun drawMarkerPoints(
        map: GoogleMap,
        markerPoints: List<MarkerPoint>,
        context: Context
    ) {
        if (drawnMarkers.isNotEmpty()) {
            removeAllMarkers(drawnMarkers)
        }

        drawAllMarkers(map, markerPoints, context)
    }

    private fun drawAllMarkers(
        map: GoogleMap,
        markerPoints: List<MarkerPoint>,
        context: Context
    ) {
        val markerOptions = MarkerOptions().visible(true).anchor(0.5f, 0.5f)
        for (markerPoint in markerPoints) {
            map.addMarker(
                markerOptions.position(markerPoint.location).icon(
                    createIcon(markerPoint.text, context)
                )
            )
        }
    }

    private fun removeAllMarkers(markers: List<Marker>) {
        for (marker in markers) {
            marker.remove()
        }
    }

    private fun createIcon(text: String, context: Context): BitmapDescriptor {
        return BitmapDescriptorFactory.fromBitmap(
            makeBitmap(text, context)
        )
    }

    private fun makeBitmap(text: String, context: Context): Bitmap {
        val resources = context.resources
        val scale = resources.displayMetrics.density
        val drawable =
            AppCompatResources.getDrawable(context, R.drawable.ic_distance_point)!!
        val bitmap = if (text.isEmpty()) {
            drawable.toBitmap(60, 60)
        } else {
            drawable.toBitmap(80, 110)
        }
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.BLACK // Text color
        paint.textSize = 14 * scale // Text size
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE) // Text shadow
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        val x = bitmap.width / 2 - 10
        val y = bitmap.height / 2
        canvas.drawText(text, x.toFloat(), y.toFloat(), paint)
        return bitmap
    }
}