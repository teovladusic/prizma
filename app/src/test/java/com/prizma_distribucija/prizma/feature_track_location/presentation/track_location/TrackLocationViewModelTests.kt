package com.prizma_distribucija.prizma.feature_track_location.presentation.track_location

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.prizma_distribucija.prizma.core.util.Constants
import com.prizma_distribucija.prizma.core.util.DispatcherProvider
import com.prizma_distribucija.prizma.core.util.TestDispatchers
import com.prizma_distribucija.prizma.feature_track_location.domain.LocationTracker
import com.prizma_distribucija.prizma.feature_track_location.domain.Timer
import com.prizma_distribucija.prizma.feature_track_location.domain.TimerImpl
import com.prizma_distribucija.prizma.feature_track_location.domain.fakes.LocationTrackerFakeImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class TrackLocationViewModelTests {

    private lateinit var viewModel: TrackLocationViewModel

    @Before
    fun setUp() {
        val testDispatchers = TestDispatchers() as DispatcherProvider
        val locationTracker = LocationTrackerFakeImpl(testDispatchers) as LocationTracker
        val timer = TimerImpl(testDispatchers) as Timer
        viewModel = TrackLocationViewModel(testDispatchers, locationTracker, timer)
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
                    cancelAndIgnoreRemainingEvents()
                }
            }

            viewModel.onStartStopClicked(true)

            job.join()
            job.cancel()
        }
}