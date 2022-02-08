package com.prizma_distribucija.prizma.feature_track_location.domain.fakes

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.prizma_distribucija.prizma.core.util.DispatcherProvider
import com.prizma_distribucija.prizma.feature_track_location.domain.LocationTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class LocationTrackerFakeImpl(
    val dispatcherProvider: DispatcherProvider
) : LocationTracker {

    companion object {
        val _isTrackingStateFlow = MutableStateFlow(false)

        val _pathPoints = MutableStateFlow(emptyList<LatLng>())
    }

    override val isTrackingStateFlow: StateFlow<Boolean>
        get() = _isTrackingStateFlow.asStateFlow()

    override val pathPoints: StateFlow<List<LatLng>>
        get() = _pathPoints.asStateFlow()


    override fun startTracking(fusedLocationProviderClient: FusedLocationProviderClient) {
        CoroutineScope(dispatcherProvider.default).launch {
            _isTrackingStateFlow.emit(true)

            while (isTrackingStateFlow.value) {
                val oldPathPoints = pathPoints.value
                val newPathPoints = mutableListOf<LatLng>()
                newPathPoints.addAll(oldPathPoints)
                val randomLatitude = Random.nextDouble()
                val randomLongitude = Random.nextDouble()
                val latLng = LatLng(randomLatitude, randomLongitude)
                newPathPoints.add(latLng)
                _pathPoints.emit(newPathPoints.toList())
            }
        }
    }

    override fun stopTracking() {
        CoroutineScope(dispatcherProvider.default).launch {
            _isTrackingStateFlow.emit(true)
        }
    }
}