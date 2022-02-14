package com.prizma_distribucija.prizma.feature_track_location.presentation.track_location

import android.location.Location
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.common.truth.Truth.assertThat
import com.prizma_distribucija.prizma.core.domain.model.User
import com.prizma_distribucija.prizma.core.util.Constants
import com.prizma_distribucija.prizma.core.util.DispatcherProvider
import com.prizma_distribucija.prizma.core.util.TestDispatchers
import com.prizma_distribucija.prizma.feature_track_location.domain.*
import com.prizma_distribucija.prizma.feature_track_location.domain.fakes.LocationTrackerFakeImpl
import com.prizma_distribucija.prizma.feature_track_location.domain.use_cases.SaveUriToRemoteDatabaseUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.math.BigDecimal

@ExperimentalCoroutinesApi
class TrackLocationViewModelTests {

    private lateinit var viewModel: TrackLocationViewModel

    @Before
    fun setUp() {
        val testDispatchers = TestDispatchers() as DispatcherProvider
        val locationTracker = LocationTrackerFakeImpl(testDispatchers) as LocationTracker
        val timer = TimerImpl(testDispatchers) as Timer
        val distanceCalculator = DistanceCalculatorImpl(testDispatchers)
        val savedStateHandle = SavedStateHandle()
        savedStateHandle.set("user", User("", "", "", ""))
        val trackLocationRepository = mock(TrackLocationRepository::class.java)
        val saveUriToRemoteDatabaseUseCase = SaveUriToRemoteDatabaseUseCase(trackLocationRepository)
        viewModel =
            TrackLocationViewModel(
                testDispatchers,
                locationTracker,
                timer,
                distanceCalculator,
                savedStateHandle,
                saveUriToRemoteDatabaseUseCase
            )
    }

    @Test
    fun onStartStopClick_hasPermissionsAndCurrentlyIsNotTracking_shouldSendCommandToForegroundServiceToStartTracking() =
        runBlocking {
            val hasPermissions = true
            LocationTrackerFakeImpl._isTrackingStateFlow.emit(false)
            assertThat(viewModel.isTracking.value).isFalse()

            val job = launch {
                viewModel.trackLocationEvents.test {
                    val emission = awaitItem()
                    assertThat(emission).isInstanceOf(TrackLocationViewModel.TrackLocationEvents.SendCommandToForegroundService::class.java)
                    assertThat((emission as TrackLocationViewModel.TrackLocationEvents.SendCommandToForegroundService).action).isEqualTo(
                        Constants.START_SERVICE_ACTION
                    )
                    cancelAndIgnoreRemainingEvents()
                }
            }

            viewModel.onStartStopClicked(hasPermissions)

            job.join()
            job.cancel()
        }

    @Test
    fun onStartStopClick_doesNotHavePermissionsAndCurrentlyIsNotTracking_shouldSendCommandToForegroundServiceToStartTrackingAndRequestPermissions() =
        runBlocking {
            val hasPermissions = false
            LocationTrackerFakeImpl._isTrackingStateFlow.emit(false)
            assertThat(viewModel.isTracking.value).isFalse()

            val job = launch {
                viewModel.trackLocationEvents.test {
                    val firstEmission = awaitItem()
                    assertThat(firstEmission).isInstanceOf(
                        TrackLocationViewModel.TrackLocationEvents.RequestPermissions::class.java
                    )

                    val secondEmission = awaitItem()
                    assertThat(secondEmission).isInstanceOf(
                        TrackLocationViewModel.TrackLocationEvents.SendCommandToForegroundService::class.java
                    )
                    assertThat(
                        (secondEmission as TrackLocationViewModel.TrackLocationEvents.SendCommandToForegroundService)
                            .action
                    ).isEqualTo(
                        Constants.START_SERVICE_ACTION
                    )
                    cancelAndIgnoreRemainingEvents()
                }
            }

            viewModel.onStartStopClicked(hasPermissions)

            job.join()
            job.cancel()
        }

    @Test
    fun onStartStopClick_CurrentlyIsTracking_shouldSendCommandToForegroundServiceToStopTracking() =
        runBlocking {
            LocationTrackerFakeImpl._isTrackingStateFlow.emit(true)

            assertThat(viewModel.isTracking.value).isTrue()

            val job = launch {
                viewModel.trackLocationEvents.test {
                    //when
                    val emission = awaitItem()
                    assertThat(emission).isInstanceOf(TrackLocationViewModel.TrackLocationEvents.SendCommandToForegroundService::class.java)
                    assertThat(
                        (emission as TrackLocationViewModel.TrackLocationEvents.SendCommandToForegroundService)
                            .action
                    ).isEqualTo(
                        Constants.STOP_SERVICE_ACTION
                    )
                    val emission2 = awaitItem()
                    assertThat(emission2).isInstanceOf(TrackLocationViewModel.TrackLocationEvents.ZoomOutToSeeEveryPathPoint::class.java)
                    cancelAndIgnoreRemainingEvents()
                }
            }

            viewModel.onStartStopClicked(true)

            job.join()
            job.cancel()
        }

    @Test
    fun `map distance travelled, correctly covert meters to km, round to 2 decimals`() = runTest {
        val dispatcherProvider: DispatcherProvider = TestDispatchers()
        val locationTracker: LocationTracker = LocationTrackerFakeImpl(dispatcherProvider)
        val timer: Timer = TimerImpl(dispatcherProvider)

        val distanceCalculator = mock(DistanceCalculator::class.java)
        val distanceTravelledInMeters = 500.0
        val results = MutableStateFlow(distanceTravelledInMeters)

        `when`(distanceCalculator.distanceTravelled).thenReturn(results)

        val savedStateHandle = SavedStateHandle()
        savedStateHandle.set("user", User("", "", "", ""))
        val trackLocationRepository = mock(TrackLocationRepository::class.java)
        val saveUriToRemoteDatabaseUseCase = SaveUriToRemoteDatabaseUseCase(trackLocationRepository)

        viewModel =
            TrackLocationViewModel(
                dispatcherProvider,
                locationTracker,
                timer,
                distanceCalculator,
                savedStateHandle,
                saveUriToRemoteDatabaseUseCase
            )

        val expected = BigDecimal(0.50).setScale(2)

        viewModel.distanceTravelled.test {
            val distanceInKm = awaitItem()
            assertThat(distanceInKm).isEqualTo(expected)
        }
    }
}