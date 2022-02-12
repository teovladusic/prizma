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

    override fun calculate(locations: List<Location>) = CoroutineScope(dispatchers.main).launch {
        if (locations.size < 2) return@launch
        val lastLocation = locations.last()
        val preLastLocation = locations.dropLast(1).last()

        val lastLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
        val preLastLatLng = LatLng(preLastLocation.latitude, preLastLocation.longitude)

        val distance = SphericalUtil.computeDistanceBetween(preLastLatLng, lastLatLng)

        val newDistance = distanceTravelled.value + distance

        _distanceTravelled.emit(newDistance)
    }
}