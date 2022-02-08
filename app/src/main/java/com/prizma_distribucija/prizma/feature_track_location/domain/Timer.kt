package com.prizma_distribucija.prizma.feature_track_location.domain

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

interface Timer {

    val formattedTimePassed: StateFlow<String>

    var isTimerEnabled: Boolean

    fun startCounting() : Job

    fun stopCounting()
}