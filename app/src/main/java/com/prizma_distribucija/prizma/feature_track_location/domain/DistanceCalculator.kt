package com.prizma_distribucija.prizma.feature_track_location.domain

import android.location.Location
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow

interface DistanceCalculator {

    val distanceTravelled: StateFlow<Double>

    fun calculate(locations: List<Location>) : Job

    fun reset()
}