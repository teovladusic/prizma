package com.prizma_distribucija.prizma.feature_track_location.domain

import android.location.Location
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.prizma_distribucija.prizma.core.util.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class DistanceCalculatorImpl @Inject constructor(
    private val dispatchers: DispatcherProvider
) : DistanceCalculator {

    private val _distanceTravelled = MutableStateFlow(0.0)

    override val distanceTravelled: StateFlow<Double>
        get() = _distanceTravelled.asStateFlow()

    private var lastMarkerDistance = 0f

    override fun calculate(locations: List<Location>) = CoroutineScope(dispatchers.default).launch {
        if (locations.size < 2) return@launch
        val lastLocation = locations.last()
        val preLastLocation = locations.dropLast(1).last()

        val lastLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
        val preLastLatLng = LatLng(preLastLocation.latitude, preLastLocation.longitude)

        val distance = calculateDistanceBetweenTwoLatLng(preLastLatLng, lastLatLng)

        val newDistance = distanceTravelled.value + distance

        _distanceTravelled.emit(newDistance)
    }

    override fun shouldAddNewMarker(): Pair<Boolean, String> {
        val shouldAddMarker = distanceTravelled.value - lastMarkerDistance >= 1000
        var text = ""

        if (shouldAddMarker) {
            lastMarkerDistance += 1000f
            text = lastMarkerDistance.toString().first().toString()
        }

        return Pair(shouldAddMarker, text)
    }

    private fun calculateDistanceBetweenTwoLatLng(latLng1: LatLng, latLng2: LatLng): Double {
        return SphericalUtil.computeDistanceBetween(latLng1, latLng2)
    }

    override fun reset() {
        CoroutineScope(dispatchers.default).launch {
            _distanceTravelled.emit(0.0)
        }
    }
}