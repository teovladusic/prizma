package com.prizma_distribucija.prizma.feature_track_location.domain.fakes

import com.prizma_distribucija.prizma.core.util.AndroidTestDispatchers
import com.prizma_distribucija.prizma.feature_track_location.domain.Timer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@ExperimentalCoroutinesApi
class TimerFakeImpl : Timer {

    private val dispatcherProvider = AndroidTestDispatchers()

    companion object {
        val _formattedTimePassed = MutableStateFlow("00:00:00")
    }

    override val formattedTimePassed: StateFlow<String> = _formattedTimePassed.asStateFlow()

    override var isTimerEnabled: Boolean = false

    var secsPassed = 0

    override fun startCounting(): Job = CoroutineScope(dispatcherProvider.main).launch {
        isTimerEnabled = true

        while (isTimerEnabled) {
            delay(1000)
            secsPassed++
            _formattedTimePassed.emit("00:00:$secsPassed")
        }
    }

    override fun stopCounting() {
        isTimerEnabled = false
    }
}