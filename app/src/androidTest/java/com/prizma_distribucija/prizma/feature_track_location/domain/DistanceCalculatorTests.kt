package com.prizma_distribucija.prizma.feature_track_location.domain

import android.location.Location
import com.prizma_distribucija.prizma.core.util.AndroidTestDispatchers
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class DistanceCalculatorTests {

    lateinit var distanceCalculator: DistanceCalculator

    @Before
    fun setUp() {
        val dispatcherProvider = AndroidTestDispatchers()
        distanceCalculator = DistanceCalculatorImpl(dispatcherProvider)
    }

    @Test
    fun calculateDistance_ProvidedOneLocation_ShouldReturn() = runTest {
        val location = Location("")
        location.latitude = 1.0
        location.longitude = 1.0

        val locations = listOf(location)

        distanceCalculator.calculate(locations)

        val distance = distanceCalculator.distanceTravelled.value
        assert(distance == 0.0)
    }

    @Test
    fun calculateDistance_ShouldCorrectlyCalculateDistance() = runTest {
        val location1 = Location("")
        location1.latitude = 45.0
        location1.longitude = 45.0

        val location2 = Location("")
        location2.latitude = 90.0
        location2.longitude = 90.0

        val locations = listOf(location1, location2)

        distanceCalculator.calculate(locations)

        //calculated distance between 2 latLng is 5000km
        val actualDistanceInKm = 5000.0

        val distanceInMeters = distanceCalculator.distanceTravelled.value
        val distanceInKm = distanceInMeters / 1000

        //distances should be correct in 10 km
        val difference = distanceInKm - actualDistanceInKm

        if (difference < 10.0 && difference > -10.0) {
            assert(true)
        } else {
            assert(false)
        }
    }
}