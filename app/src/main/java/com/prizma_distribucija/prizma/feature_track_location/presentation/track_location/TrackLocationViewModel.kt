package com.prizma_distribucija.prizma.feature_track_location.presentation.track_location

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.prizma_distribucija.prizma.core.util.Constants
import com.prizma_distribucija.prizma.core.util.DispatcherProvider
import com.prizma_distribucija.prizma.feature_track_location.domain.GoogleMapManager
import com.prizma_distribucija.prizma.feature_track_location.domain.LocationTracker
import com.prizma_distribucija.prizma.feature_track_location.domain.LocationTrackerImpl
import com.prizma_distribucija.prizma.feature_track_location.domain.Timer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackLocationViewModel @Inject constructor(
    private val dispatchers: DispatcherProvider,
    private val locationTracker: LocationTracker,
    private val timer: Timer
) : ViewModel() {

    val isTracking = locationTracker.isTrackingStateFlow

    val pathPoints = locationTracker.pathPoints

    private val trackLocationEventsChannel = Channel<TrackLocationEvents>()
    val trackLocationEvents = trackLocationEventsChannel.receiveAsFlow()

    val timePassed = timer.formattedTimePassed

    fun onStartStopClicked(hasPermissions: Boolean) {
        if (isTracking.value) {
            stopTracking()
        } else {
            startTracking(hasPermissions)
        }
    }

    private fun startTracking(hasPermissions: Boolean) {
        if (hasPermissions == false) {
            sendTrackLocationEvent(TrackLocationEvents.RequestPermissions)
            return
        }
        val event =
            TrackLocationEvents.SendCommandToForegroundService(Constants.START_SERVICE_ACTION)
        sendTrackLocationEvent(event)
    }

    private fun stopTracking() {
        val event =
            TrackLocationEvents.SendCommandToForegroundService(Constants.STOP_SERVICE_ACTION)
        sendTrackLocationEvent(event)
    }

    private fun sendTrackLocationEvent(event: TrackLocationEvents) =
        viewModelScope.launch(dispatchers.default) {
            trackLocationEventsChannel.send(event)
        }

    sealed class TrackLocationEvents {
        object RequestPermissions : TrackLocationEvents()
        data class SendCommandToForegroundService(val action: String) : TrackLocationEvents()
    }
}