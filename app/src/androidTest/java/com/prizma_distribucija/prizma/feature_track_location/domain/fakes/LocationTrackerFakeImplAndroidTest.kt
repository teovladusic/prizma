package com.prizma_distribucija.prizma.feature_track_location.domain.fakes

import android.location.Location
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

        val _locations = MutableStateFlow(emptyList<Location>())

        fun setDefaults() {
            CoroutineScope(UnconfinedTestDispatcher()).launch {
                _isTrackingStateFlow.emit(false)
                _locations.emit(emptyList())
            }
        }
    }

    override val isTrackingStateFlow: StateFlow<Boolean>
        get() = _isTrackingStateFlow.asStateFlow()

    override val locations: StateFlow<List<Location>>
        get() = _locations.asStateFlow()


    override fun startTracking(fusedLocationProviderClient: FusedLocationProviderClient) {
        CoroutineScope(dispatcherProvider.default).launch {
            _isTrackingStateFlow.emit(true)

            while (isTrackingStateFlow.value) {
                val oldLocations = locations.value
                val newLocations = mutableListOf<Location>()
                newLocations.addAll(oldLocations)
                val latitude = oldLocations.size + 1
                val longitude = oldLocations.size + 1

                val location = createLocation(latitude.toDouble(), longitude.toDouble())
                newLocations.add(location)
                _locations.emit(newLocations.toList())
                delay(500L)
            }
        }
    }

    private fun createLocation(latitude: Double, longitude: Double) : Location {
        val location = Location("")
        location.latitude = latitude
        location.longitude = longitude
        return location
    }

    override fun stopTracking() {
        CoroutineScope(dispatcherProvider.default).launch {
            _isTrackingStateFlow.emit(false)
        }
    }
}