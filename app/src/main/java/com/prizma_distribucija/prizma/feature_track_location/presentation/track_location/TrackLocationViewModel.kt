package com.prizma_distribucija.prizma.feature_track_location.presentation.track_location

import android.annotation.SuppressLint
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.prizma_distribucija.prizma.core.domain.model.User
import com.prizma_distribucija.prizma.core.util.Constants
import com.prizma_distribucija.prizma.core.util.DispatcherProvider
import com.prizma_distribucija.prizma.core.util.Resource
import com.prizma_distribucija.prizma.feature_track_location.domain.*
import com.prizma_distribucija.prizma.feature_track_location.domain.Timer
import com.prizma_distribucija.prizma.feature_track_location.domain.use_cases.SaveUriToRemoteDatabaseUseCase
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
    timer: Timer,
    distanceCalculator: DistanceCalculator,
    savedStateHandle: SavedStateHandle,
    private val saveUriToRemoteDatabaseUseCase: SaveUriToRemoteDatabaseUseCase
) : ViewModel() {

    val user = savedStateHandle.get<User>("user")

    val isTracking = locationTracker.isTrackingStateFlow

    val locations = locationTracker.locations

    private val trackLocationEventsChannel = Channel<TrackLocationEvents>()
    val trackLocationEvents = trackLocationEventsChannel.receiveAsFlow()

    val timePassed = timer.formattedTimePassed

    val distanceTravelled = distanceCalculator.distanceTravelled.map { distance ->
        val rounded = BigDecimal(distance / 1000).setScale(2, RoundingMode.HALF_EVEN)
        rounded
    }

    private val _savingStatus = MutableSharedFlow<Resource<Boolean>>()
    val savingStatus = _savingStatus.asSharedFlow()

    var uri: Uri? = null

    fun onStartStopClicked(hasPermissions: Boolean) {
        if (isTracking.value) {
            stopTracking()
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

        TrackLocationEvents.ZoomOutToSeeEveryPathPoint.also {
            sendTrackLocationEvent(it)
        }
    }

    fun saveUriToDatabase(uri: Uri) = viewModelScope.launch(dispatchers.io) {
        val avgSpeed = calculateAvgSpeed(
            TrackingForegroundService.timeFinished,
            TrackingForegroundService.timeStarted,
            TrackingForegroundService.distance
        )
        TrackingForegroundService.avgSpeed = avgSpeed.toString()
        val path = getPath()
        saveUriToRemoteDatabaseUseCase(path, uri).collectLatest {
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
        val path = getPath()
        createAndBeginWorkManagerRequest(path, workManager)
    }

    private fun createAndBeginWorkManagerRequest(path: String, workManager: WorkManager) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val inputData = workDataOf(Pair("uri", uri.toString()), Pair("path", path))

        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<SendUriWorker>()
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


    private fun getPath(): String {
        val date = getDate(Date())
        val timeStarted = TrackingForegroundService.timeStarted
        val timeFinished = TrackingForegroundService.timeFinished
        val distance = TrackingForegroundService.distance
        val avgSpeed = TrackingForegroundService.avgSpeed
        return "${user!!.name}_${user.lastName}/$date, $timeStarted - $timeFinished, $distance km, $avgSpeed km|h"
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
        object ZoomOutToSeeEveryPathPoint : TrackLocationEvents()
    }
}