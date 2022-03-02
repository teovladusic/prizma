package com.prizma_distribucija.prizma.feature_track_location.presentation.track_location

import android.location.Location
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.google.android.gms.maps.SupportMapFragment
import com.prizma_distribucija.prizma.R
import com.prizma_distribucija.prizma.core.di.SingletonModule
import com.prizma_distribucija.prizma.core.util.Constants
import com.prizma_distribucija.prizma.feature_track_location.domain.*
import com.prizma_distribucija.prizma.feature_track_location.domain.fakes.GoogleMapManagerFakeImpl
import com.prizma_distribucija.prizma.feature_track_location.domain.fakes.LocationTrackerFakeImplAndroidTest
import com.prizma_distribucija.prizma.feature_track_location.domain.fakes.PermissionManagerFakeImpl
import com.prizma_distribucija.prizma.feature_track_location.domain.fakes.TimerFakeImpl
import com.prizma_distribucija.prizma.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@MediumTest
@ExperimentalCoroutinesApi
@HiltAndroidTest
@UninstallModules(SingletonModule::class)
class TrackLocationFragmentTests {

    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltAndroidRule.inject()
        PermissionManagerFakeImpl.setDefaults()
    }

    @Test
    fun onStartStopBtnClick_ProperlyChangeTextOfBtn() {
        PermissionManagerFakeImpl.hasPermissions = true

        launchFragmentInHiltContainer<TrackLocationFragment> {
        }

        assert(LocationTrackerFakeImplAndroidTest._isTrackingStateFlow.value == false)

        //check if the default text is correct
        onView(withId(R.id.btn_stop_start)).check(
            matches(withText(Constants.START_TRACKING_BUTTON_TEXT))
        )

        //start tracking
        onView(withId(R.id.btn_stop_start)).perform(
            click()
        )

        assert(LocationTrackerFakeImplAndroidTest._isTrackingStateFlow.value == true)

        //check if the text is properly changed
        onView(withId(R.id.btn_stop_start)).check(
            matches(withText(Constants.STOP_TRACKING_BUTTON_TEXT))
        )

        //stop tracking
        onView(withId(R.id.btn_stop_start)).perform(
            click()
        )

        assert(LocationTrackerFakeImplAndroidTest._isTrackingStateFlow.value == false)

        //check if the text is properly changed
        onView(withId(R.id.btn_stop_start)).check(
            matches(withText(Constants.START_TRACKING_BUTTON_TEXT))
        )
    }

    @Test
    fun requestPermissions_ifPermissionsGrantedDialogShouldNotExist_ifPermissionsNotGrantedShouldRequestAndDisplayDialog() {
        var hasPermissions = false

        val permissionManagerImpl = PermissionManagerImpl()
        launchFragmentInHiltContainer<TrackLocationFragment> {
            permissionManager = permissionManagerImpl
            hasPermissions = permissionManager.hasPermissions(requireContext())
        }

        if (hasPermissions) {
            onView(withText(Constants.REQUEST_PERMISSION_MESSAGE)).check(
                doesNotExist()
            )
        } else {
            onView(withText(Constants.REQUEST_PERMISSION_MESSAGE)).check(
                matches(
                    isDisplayed()
                )
            )
        }
    }

    @Test
    fun startStopBtnClick_checkForPermissions_DoesNotHavePermissions_RequestThem() {
        PermissionManagerFakeImpl.hasPermissions = false

        launchFragmentInHiltContainer<TrackLocationFragment> {
        }

        //verify it's not already tracking (should not start tracking if permissions are not granted)
        onView(withId(R.id.btn_stop_start)).check(matches(withText(Constants.START_TRACKING_BUTTON_TEXT)))

        onView(withId(R.id.btn_stop_start)).perform(click())

        assert(PermissionManagerFakeImpl.havePermissionsBeenRequested == true)
    }

    @Test
    fun startStopBtnClick_checkForPermissions_hasPermissions_shouldNotRequestThem() {
        PermissionManagerFakeImpl.hasPermissions = true
        launchFragmentInHiltContainer<TrackLocationFragment> {
        }

        //verify it's not already tracking (should not start tracking if permissions are not granted)
        onView(withId(R.id.btn_stop_start)).check(matches(withText(Constants.START_TRACKING_BUTTON_TEXT)))

        onView(withId(R.id.btn_stop_start)).perform(click())

        assert(PermissionManagerFakeImpl.havePermissionsBeenRequested == false)
    }

    @Test
    fun startTrackingClick_sendProperCommandToForegroundService() {
        launchFragmentInHiltContainer<TrackLocationFragment> {
        }

        PermissionManagerFakeImpl.hasPermissions = true

        assert(LocationTrackerFakeImplAndroidTest._isTrackingStateFlow.value == false)

        onView(withId(R.id.btn_stop_start)).perform(
            click()
        )

        //command is sent to foreground service which calls locationTracker.startTracking() function
        //cannot get the viewModel (private) to check for events
        assert(LocationTrackerFakeImplAndroidTest._isTrackingStateFlow.value == true)
    }

    @Test
    fun stopTrackingClick_sendProperCommandToForegroundService() {
        launchFragmentInHiltContainer<TrackLocationFragment> {
        }

        PermissionManagerFakeImpl.hasPermissions = true

        runBlocking {
            LocationTrackerFakeImplAndroidTest._isTrackingStateFlow.emit(true)
        }

        assert(LocationTrackerFakeImplAndroidTest._isTrackingStateFlow.value == true)

        onView(withId(R.id.btn_stop_start)).perform(
            click()
        )

        //command is sent to foreground service which calls locationTracker.stopTracking() function
        //cannot get the viewModel (private) to check for events
        assert(LocationTrackerFakeImplAndroidTest._isTrackingStateFlow.value == false)
    }

    @Test
    fun onNewPathPoint_callGoogleMapManagerOnNewPathPointsWithRightData() {
        launchFragmentInHiltContainer<TrackLocationFragment> {
        }

        PermissionManagerFakeImpl.hasPermissions = true

        //start tracking
        onView(withId(R.id.btn_stop_start)).perform(
            click()
        )

        val location = Location("")
        location.latitude = 1.0
        location.longitude = 1.0
        assert(LocationTrackerFakeImplAndroidTest._locations.value.first() == location)


        assert(GoogleMapManagerFakeImpl.isOnNewPathPointsCalled == true)
    }

    @Test
    fun onFragmentLaunch_isMapsStyleSet() {
        GoogleMapManagerFakeImpl.isSetStyleCalled = false
        launchFragmentInHiltContainer<TrackLocationFragment> {
            val supportMapFragment =
                childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment?
            supportMapFragment?.getMapAsync { _ ->
                assert(GoogleMapManagerFakeImpl.isSetStyleCalled == true)
            }
        }
    }

    @Test
    fun onTimerEmitValue_setText() = runTest {
        launchFragmentInHiltContainer<TrackLocationFragment> {
        }

        TimerFakeImpl._formattedTimePassed.emit("00:00:01")

        onView(withId(R.id.tv_time)).check(matches(withText("00:00:01")))
    }

    @Test
    fun onStopClick_shouldCallGoogleMapsManagerToZoomOut() {
        launchFragmentInHiltContainer<TrackLocationFragment> {
        }

        PermissionManagerFakeImpl.hasPermissions = true

        assert(LocationTrackerFakeImplAndroidTest._isTrackingStateFlow.value == false)

        onView(withId(R.id.btn_stop_start)).perform(
            click()
        )

        assert(LocationTrackerFakeImplAndroidTest._isTrackingStateFlow.value)

        onView(withId(R.id.btn_stop_start)).perform(
            click()
        )

        assert(LocationTrackerFakeImplAndroidTest._isTrackingStateFlow.value == false)

        assert(GoogleMapManagerFakeImpl.hasZoomedOut)
    }
}