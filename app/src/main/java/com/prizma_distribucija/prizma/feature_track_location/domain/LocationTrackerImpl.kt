package com.prizma_distribucija.prizma.feature_track_location.domain

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import androidx.annotation.VisibleForTesting
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.prizma_distribucija.prizma.core.util.Constants.FASTEST_LOCATION_INTERVAL
import com.prizma_distribucija.prizma.core.util.Constants.LOCATION_UPDATE_INTERVAL
import com.prizma_distribucija.prizma.core.util.DispatcherProvider
import com.prizma_distribucija.prizma.feature_track_location.domain.model.MarkerPoint
import com.prizma_distribucija.prizma.feature_track_location.presentation.track_location.TrackingForegroundService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

class LocationTrackerImpl @Inject constructor(
    private val dispatchers: DispatcherProvider,
    private val distanceCalculator: DistanceCalculator
) : LocationTracker {

    private val _isTrackingStateFlow = MutableStateFlow(false)
    override val isTrackingStateFlow: StateFlow<Boolean> = _isTrackingStateFlow

    private val _markerPoints = MutableStateFlow(emptyList<MarkerPoint>())
    override val markerPoints = _markerPoints.asStateFlow()

    private val _locations = MutableStateFlow(emptyList<Location>())
    override val locations = _locations.asStateFlow()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun startTracking(fusedLocationProviderClient: FusedLocationProviderClient) {
        resetPathPoints()
        resetDistanceCalculator()
        setFusedLocationProviderClient(fusedLocationProviderClient)
        CoroutineScope(dispatchers.default).launch {
            _isTrackingStateFlow.emit(true)
            requestLocationUpdates()
        }
    }

    private fun resetDistanceCalculator() {
        distanceCalculator.reset()
    }

    private fun resetPathPoints() = CoroutineScope(dispatchers.default).launch {
        val newLocations = emptyList<Location>()
        _locations.emit(newLocations)
    }

    private fun setFusedLocationProviderClient(fusedLocationProviderClient: FusedLocationProviderClient) {
        this.fusedLocationProviderClient = fusedLocationProviderClient
    }

    override fun stopTracking() {
        CoroutineScope(dispatchers.default).launch {
            _isTrackingStateFlow.emit(false)
            removeLocationUpdates()
            TrackingForegroundService.distance =
                convertDistanceInMetersToKm(distanceCalculator.distanceTravelled.value).toString()
        }
    }

    private fun convertDistanceInMetersToKm(distance: Double): BigDecimal {
        return BigDecimal(distance / 1000).setScale(2, RoundingMode.HALF_EVEN)
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        val locationRequest = getLocationRequest()
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            getMainLooper()
        )
    }

    private fun getMainLooper(): Looper =
        Looper.getMainLooper()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval = LOCATION_UPDATE_INTERVAL
            fastestInterval = FASTEST_LOCATION_INTERVAL
            priority = PRIORITY_HIGH_ACCURACY
        }
    }

    private fun removeLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)

            result.locations.apply {
                for (location in this) {
                    addLocationAndCalculateDistance(location)
                }
            }
        }
    }

    private fun addLocationAndCalculateDistance(location: Location) =
        CoroutineScope(dispatchers.default).launch {
            val newLocations = mutableListOf<Location>()
            newLocations.addAll(locations.value)
            newLocations.add(location)
            distanceCalculator.calculate(newLocations.toList())
            addNewMarkerPointIfNeeded(location)
            _locations.emit(newLocations)
        }

    private suspend fun addNewMarkerPointIfNeeded(location: Location) {
        val shouldAddNewMarker = distanceCalculator.shouldAddNewMarker()
        if (shouldAddNewMarker.first) {
            val newMarkerPoints = markerPoints.value.toMutableList()
            val latLng = LatLng(location.latitude, location.longitude)
            val text = shouldAddNewMarker.second
            newMarkerPoints.add(MarkerPoint(latLng, text))
            _markerPoints.emit(newMarkerPoints)
        }
    }
}