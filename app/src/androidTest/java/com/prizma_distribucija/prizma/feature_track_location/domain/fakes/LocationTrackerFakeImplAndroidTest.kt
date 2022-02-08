package com.prizma_distribucija.prizma.feature_track_location.domain.fakes

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.prizma_distribucija.prizma.core.util.DispatcherProvider
import com.prizma_distribucija.prizma.feature_track_location.domain.LocationTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher

class LocationTrackerFakeImplAndroidTest(
    val dispatcherProvider: DispatcherProvider
) : LocationTracker {

    @ExperimentalCoroutinesApi
    companion object {
        val _isTrackingStateFlow = MutableStateFlow(false)

        val _pathPoints = MutableStateFlow(emptyList<LatLng>())

        fun setDefaults() {
            CoroutineScope(UnconfinedTestDispatcher()).launch {
                _isTrackingStateFlow.emit(false)
                _pathPoints.emit(emptyList())
            }
        }
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
                val latitude = oldPathPoints.size + 1
                val longitude = oldPathPoints.size + 1
                val latLng = LatLng(latitude.toDouble(), longitude.toDouble())
                newPathPoints.add(latLng)
                _pathPoints.emit(newPathPoints.toList())
                delay(500L)
            }
        }
    }

    override fun stopTracking() {
        CoroutineScope(dispatcherProvider.default).launch {
            _isTrackingStateFlow.emit(false)
        }
    }
}