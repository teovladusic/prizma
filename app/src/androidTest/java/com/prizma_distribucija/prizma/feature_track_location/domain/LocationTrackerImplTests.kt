package com.prizma_distribucija.prizma.feature_track_location.domain

import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.MediumTest
import com.google.android.gms.location.*
import com.prizma_distribucija.prizma.core.di.SingletonModule
import com.prizma_distribucija.prizma.core.util.AndroidTestDispatchers
import com.prizma_distribucija.prizma.core.util.Constants
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

@HiltAndroidTest
@ExperimentalCoroutinesApi
@MediumTest
@UninstallModules(SingletonModule::class)
class LocationTrackerImplTests {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var locationTracker: LocationTrackerImpl

    lateinit var context: Context

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Before
    fun setUp() {
        hiltRule.inject()
        val testDispatchers = AndroidTestDispatchers()
        val distanceCalculator: DistanceCalculator = DistanceCalculatorImpl(testDispatchers)
        locationTracker = LocationTrackerImpl(testDispatchers, distanceCalculator)
        context = ApplicationProvider.getApplicationContext()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    }

    @Test
    fun startTracking_emitIsTrackingTrue() = runTest {
        assert(locationTracker.isTrackingStateFlow.value == false)

        locationTracker.startTracking(fusedLocationProviderClient)

        fusedLocationProviderClient.removeLocationUpdates(locationTracker.locationCallback)

        assert(locationTracker.isTrackingStateFlow.value == true)

        locationTracker.stopTracking()
    }

    @Test
    fun stopTracking_emitsIsTrackingFalse() = runTest {
        locationTracker.startTracking(fusedLocationProviderClient)

        fusedLocationProviderClient.removeLocationUpdates(locationTracker.locationCallback)

        assert(locationTracker.isTrackingStateFlow.value == true)

        locationTracker.stopTracking()

        assert(locationTracker.isTrackingStateFlow.value == false)
    }

    @Test
    fun startTracking_shouldRequestLocationUpdates() {
        val fusedLocationProviderClientMock = mock(FusedLocationProviderClient::class.java)

        locationTracker.startTracking(fusedLocationProviderClientMock)

        verify(fusedLocationProviderClientMock, times(1)).requestLocationUpdates(
            locationTracker.getLocationRequest(),
            locationTracker.locationCallback,
            Looper.getMainLooper()
        )

        locationTracker.stopTracking()
    }

    @Test
    fun getLocationRequest_returnsCorrectLocationRequest() {
        val request = locationTracker.getLocationRequest()

        val requestInterval = Constants.LOCATION_UPDATE_INTERVAL
        val fastestRequestInterval = Constants.FASTEST_LOCATION_INTERVAL
        val priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        assert(request.interval == requestInterval)
        assert(request.fastestInterval == fastestRequestInterval)
        assert(request.priority == priority)
    }

    @Test
    fun stopTracking_removesLocationUpdates() {
        val fusedLocationProviderClientMock = mock(FusedLocationProviderClient::class.java)

        //cannot stop tracking if it's not already tracking
        locationTracker.startTracking(fusedLocationProviderClientMock)

        locationTracker.stopTracking()

        verify(fusedLocationProviderClientMock, times(1))
            .removeLocationUpdates(locationTracker.locationCallback)
    }

    @Test
    fun onLocationResult_correctlyAddsAllPathPointsAndCalculatesDistance() {
        val location1 = Location("")
        location1.latitude = 1.0
        location1.longitude = 1.0

        val location2 = Location("")
        location2.latitude = 2.0
        location2.longitude = 2.0

        val dispatchers = AndroidTestDispatchers()
        val distanceCalculator = mock(DistanceCalculator::class.java)

        val locationTracker = LocationTrackerImpl(dispatchers, distanceCalculator)

        val locationResult = LocationResult.create(listOf(location1, location2))

        assert(locationTracker.locations.value.isEmpty())

        locationTracker.locationCallback.onLocationResult(locationResult)

        assert(locationTracker.locations.value[0] == location1)
        assert(locationTracker.locations.value[1] == location2)

        verify(distanceCalculator).calculate(locationTracker.locations.value.toList())
    }

    @Test
    fun startTracking_shouldSetPathPointsToEmptyList() {
        val location1 = Location("")
        location1.latitude = 1.0
        location1.longitude = 1.0

        val location2 = Location("")
        location2.latitude = 2.0
        location2.longitude = 2.0

        val locationResult = LocationResult.create(listOf(location1, location2))

        locationTracker.locationCallback.onLocationResult(locationResult)

        assert(locationTracker.locations.value[0] == location1)
        assert(locationTracker.locations.value[1] == location2)

        locationTracker.startTracking(fusedLocationProviderClient)

        assert(locationTracker.locations.value.isEmpty())

        locationTracker.stopTracking()
    }

    @Test
    fun onStart_shouldResetDistance() = runTest {
        val distanceCalculator = mock(DistanceCalculator::class.java)
        val dispatchers = AndroidTestDispatchers()
        val locationTracker =
            LocationTrackerImpl(dispatchers, distanceCalculator) as LocationTracker

        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        locationTracker.startTracking(fusedLocationProviderClient)

        verify(distanceCalculator).reset()
    }

    @Test
    fun addNewMarkerIfNeeded_shouldAddNewMarker() = runTest {
        val dispatchers = AndroidTestDispatchers()
        val distanceCalculator = mock(DistanceCalculator::class.java)

        `when`(distanceCalculator.shouldAddNewMarker()).thenReturn(true to "1")

        val locationTracker = LocationTrackerImpl(dispatchers, distanceCalculator)

        val location1 = Location("")
        location1.latitude = 45.0
        location1.longitude = 45.0

        val location2 = Location("")
        location2.latitude = 90.0
        location2.longitude = 90.0

        assert(locationTracker.markerPoints.value.isEmpty())

        val locationResult = LocationResult.create(listOf(location1, location2))

        locationTracker.locationCallback.onLocationResult(locationResult)

        assert(locationTracker.markerPoints.value.isNotEmpty())
    }

    @Test
    fun addNewMarkerIfNeeded_shouldNotAddNewMarker() = runTest {
        val dispatchers = AndroidTestDispatchers()
        val distanceCalculator = mock(DistanceCalculator::class.java)

        `when`(distanceCalculator.shouldAddNewMarker()).thenReturn(false to "")

        val locationTracker = LocationTrackerImpl(dispatchers, distanceCalculator)

        val location1 = Location("")
        location1.latitude = 45.0
        location1.longitude = 45.0

        val location2 = Location("")
        location2.latitude = 90.0
        location2.longitude = 90.0

        assert(locationTracker.markerPoints.value.isEmpty())

        val locationResult = LocationResult.create(listOf(location1, location2))

        locationTracker.locationCallback.onLocationResult(locationResult)

        assert(locationTracker.markerPoints.value.isEmpty())
    }

}