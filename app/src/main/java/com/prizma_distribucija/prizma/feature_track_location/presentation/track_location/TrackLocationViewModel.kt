package com.prizma_distribucija.prizma.feature_track_location.presentation.track_location

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prizma_distribucija.prizma.core.util.Constants
import com.prizma_distribucija.prizma.core.util.DispatcherProvider
import com.prizma_distribucija.prizma.feature_track_location.domain.DistanceCalculator
import com.prizma_distribucija.prizma.feature_track_location.domain.LocationTracker
import com.prizma_distribucija.prizma.feature_track_location.domain.Timer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import kotlin.math.roundToLong

@HiltViewModel
class TrackLocationViewModel @Inject constructor(
    private val dispatchers: DispatcherProvider,
    private val locationTracker: LocationTracker,
    private val timer: Timer,
    private val distanceCalculator: DistanceCalculator
) : ViewModel() {

    val isTracking = locationTracker.isTrackingStateFlow

    val locations = locationTracker.locations

    private val trackLocationEventsChannel = Channel<TrackLocationEvents>()
    val trackLocationEvents = trackLocationEventsChannel.receiveAsFlow()

    val timePassed = timer.formattedTimePassed

    val distanceTravelled = distanceCalculator.distanceTravelled.map { distance ->
        val rounded = BigDecimal(distance / 1000).setScale(2, RoundingMode.HALF_EVEN)
        rounded
    }

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