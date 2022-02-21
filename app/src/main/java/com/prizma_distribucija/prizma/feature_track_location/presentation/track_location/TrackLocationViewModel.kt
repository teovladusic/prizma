package com.prizma_distribucija.prizma.feature_track_location.presentation.track_location

import android.annotation.SuppressLint
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.prizma_distribucija.prizma.core.domain.model.User
import com.prizma_distribucija.prizma.core.util.Constants
import com.prizma_distribucija.prizma.core.util.DispatcherProvider
import com.prizma_distribucija.prizma.core.util.Resource
import com.prizma_distribucija.prizma.core.util.formatDateInMillisToString
import com.prizma_distribucija.prizma.feature_track_location.domain.*
import com.prizma_distribucija.prizma.feature_track_location.domain.Timer
import com.prizma_distribucija.prizma.feature_track_location.domain.model.Route
import com.prizma_distribucija.prizma.feature_track_location.domain.use_cases.SaveRouteToRemoteDatabaseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class TrackLocationViewModel @Inject constructor(
    private val dispatchers: DispatcherProvider,
    locationTracker: LocationTracker,
    private val timer: Timer,
    private val distanceCalculator: DistanceCalculator,
    savedStateHandle: SavedStateHandle,
    private val saveRouteToRemoteDatabaseUseCase: SaveRouteToRemoteDatabaseUseCase
) : ViewModel() {

    val user = savedStateHandle.get<User>("user")

    val isTracking = locationTracker.isTrackingStateFlow

    val locations = locationTracker.locations

    val distanceMarkerPoints = locationTracker.markerPoints

    private val trackLocationEventsChannel = Channel<TrackLocationEvents>()
    val trackLocationEvents = trackLocationEventsChannel.receiveAsFlow()

    val timePassed = timer.formattedTimePassed


    val distanceTravelled = distanceCalculator.distanceTravelled.map { distance ->
        roundOn2Decimals(distance / 1000)
    }

    private fun roundOn2Decimals(number: Double): BigDecimal {
        return BigDecimal(number).setScale(2, RoundingMode.HALF_EVEN)
    }

    private val _savingStatus = MutableSharedFlow<Resource<Boolean>>()
    val savingStatus = _savingStatus.asSharedFlow()

    fun onStartStopClicked(hasPermissions: Boolean) {
        if (isTracking.value) {
            stopTracking()
            saveRouteToDatabase()
        } else {
            startTracking(hasPermissions)
            TrackingForegroundService.user = user!!
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
        TrackLocationEvents.SendCommandToForegroundService(Constants.STOP_SERVICE_ACTION).also {
            sendTrackLocationEvent(it)
        }
    }

    private fun saveRouteToDatabase() = viewModelScope.launch(dispatchers.io) {
        val route = getRoute()
        saveRouteToRemoteDatabaseUseCase(route).collectLatest {
            _savingStatus.emit(it)
        }
    }

    private fun calculateAvgSpeed(
        timeFinished: String,
        timeStarted: String,
        distance: String
    ): Long {
        val hourStarted = timeStarted.take(2).toInt()
        val minutesStarted = timeStarted.takeLast(2).toInt()

        val hourFinished = timeFinished.take(2).toInt()
        val minuteFinished = timeFinished.takeLast(2).toInt()

        val timeStartedInSeconds = (hourStarted * 60 * 60) + (minutesStarted * 60)
        val timeFinishedInSeconds = (hourFinished * 60 * 60) + (minuteFinished * 60)

        val timeNeededInSecs = timeFinishedInSeconds - timeStartedInSeconds

        if (timeNeededInSecs == 0) {
            return 0
        }

        val distanceInKm = distance.toDouble()

        return distanceInKm.toLong() / timeNeededInSecs
    }

    fun onSendLaterClick(workManager: WorkManager) {
        val route = getRoute()
        createAndBeginWorkManagerRequest(workManager, route)
    }

    private fun getRoute(): Route {
        val pathPoints = locations.value.map { LatLng(it.latitude, it.longitude) }
        val timeStarted = formatDateInMillisToString(timer.timeStarted)
        val timeFinished = formatDateInMillisToString(timer.timeFinished)
        val distance = roundOn2Decimals(distanceCalculator.distanceTravelled.value).toString()
        val date = getDate(Date())
        val userId = TrackingForegroundService.user.userId
        val year = date.takeLast(4)
        val month = "${date[3]}${date[4]}"
        val day = date.take(2)

        val avgSpeed = calculateAvgSpeed(
            timeFinished,
            timeStarted,
            distance
        )

        return Route(
            avgSpeed = avgSpeed.toString(),
            distanceTravelled = distance,
            month = month.toInt(),
            pathPoints = pathPoints,
            timeFinished = timeFinished,
            timeStarted = timeStarted,
            userId = userId,
            year = year.toInt(),
            day = day.toInt()
        )
    }

    private fun createAndBeginWorkManagerRequest(workManager: WorkManager, route: Route) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        //save json data to workmanager
        val gson = Gson()
        val data = gson.toJson(route)

        val inputData = workDataOf("route" to data)

        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<SaveRouteWhenConnectionAvailableWorker>()
            .setInputData(inputData)
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        workManager.beginWith(oneTimeWorkRequest).enqueue()
    }

    @SuppressLint("SimpleDateFormat")
    fun getDate(mDate: Date): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        var date = sdf.format(mDate)
        date = date.replace("/", ".")
        return date
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