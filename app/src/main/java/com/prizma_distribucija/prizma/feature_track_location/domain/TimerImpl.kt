package com.prizma_distribucija.prizma.feature_track_location.domain

import android.util.Log
import com.prizma_distribucija.prizma.core.util.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TimerImpl @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
) : Timer {

    override var isTimerEnabled: Boolean = false

    private val _formattedTimePassed = MutableStateFlow("00:00:00")
    override val formattedTimePassed: StateFlow<String> = _formattedTimePassed.asStateFlow()

    private var timeStarted = 0L

    override fun startCounting() = CoroutineScope(dispatcherProvider.default).launch {
        isTimerEnabled = true
        timeStarted = System.currentTimeMillis()

        while (isTimerEnabled) {
            calculateTimeAndEmitNewTime()
            delay(1000)
        }
    }

    private suspend fun calculateTimeAndEmitNewTime() {
        val currentTimeInMillis = System.currentTimeMillis()
        val timeDifferenceInMillis = currentTimeInMillis - timeStarted
        val formattedTimeDifference = getFormattedStopWatchTime(timeDifferenceInMillis)
        _formattedTimePassed.emit(formattedTimeDifference)
    }

    private fun getFormattedStopWatchTime(ms: Long): String {
        if (ms < 0) {
            Log.e("Timer", "milliseconds less than zero")
            return "00:00:00"
        }

        var milliseconds = ms

        //getting how much whole hours milliseconds have
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)

        //removing the hours from the milliseconds
        milliseconds -= TimeUnit.HOURS.toMillis(hours)

        //getting how much whole minutes milliseconds have
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)

        //removing the minutes from the milliseconds
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)

        //getting how much whole seconds are left
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)

        return "${if (hours < 10) "0" else ""}$hours:" +
                "${if (minutes < 10) "0" else ""}$minutes:" +
                "${if (seconds < 10) "0" else ""}$seconds"
    }

    override fun stopCounting() {
        isTimerEnabled = false
    }
}