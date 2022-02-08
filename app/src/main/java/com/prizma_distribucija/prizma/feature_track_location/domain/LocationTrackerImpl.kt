package com.prizma_distribucija.prizma.feature_track_location.domain

import android.annotation.SuppressLint
import android.os.Looper
import androidx.annotation.VisibleForTesting
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import com.prizma_distribucija.prizma.core.util.Constants.FASTEST_LOCATION_INTERVAL
import com.prizma_distribucija.prizma.core.util.Constants.LOCATION_UPDATE_INTERVAL
import com.prizma_distribucija.prizma.core.util.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocationTrackerImpl @Inject constructor(
    private val dispatchers: DispatcherProvider
) : LocationTracker {

    private val _isTrackingStateFlow = MutableStateFlow(false)
    override val isTrackingStateFlow: StateFlow<Boolean> = _isTrackingStateFlow

    private val _pathPoints = MutableStateFlow(emptyList<LatLng>())
    override val pathPoints = _pathPoints.asStateFlow()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun startTracking(fusedLocationProviderClient: FusedLocationProviderClient) {
        resetPathPoints()
        setFusedLocationProviderClient(fusedLocationProviderClient)
        CoroutineScope(dispatchers.default).launch {
            _isTrackingStateFlow.emit(true)
            requestLocationUpdates()
        }
    }

    private fun resetPathPoints() = CoroutineScope(dispatchers.default).launch {
        val newPathPoints = emptyList<LatLng>()
        _pathPoints.emit(newPathPoints)
    }

    private fun setFusedLocationProviderClient(fusedLocationProviderClient: FusedLocationProviderClient) {
        this.fusedLocationProviderClient = fusedLocationProviderClient
    }

    override fun stopTracking() {
        CoroutineScope(dispatchers.default).launch {
            _isTrackingStateFlow.emit(false)
            removeLocationUpdates()
        }
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
                    addPathPoint(location.latitude, location.longitude)
                }
            }
        }
    }

    private fun addPathPoint(latitude: Double, longitude: Double) =
        CoroutineScope(dispatchers.default).launch {
            val latLng = LatLng(latitude, longitude)
            val newPathPointList = mutableListOf<LatLng>()
            newPathPointList.addAll(pathPoints.value)
            newPathPointList.add(latLng)
            _pathPoints.emit(newPathPointList)
        }
}