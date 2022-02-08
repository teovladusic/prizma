package com.prizma_distribucija.prizma.feature_track_location.presentation.track_location

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.prizma_distribucija.prizma.R
import com.prizma_distribucija.prizma.core.di.CoreModule
import com.prizma_distribucija.prizma.core.util.Constants
import com.prizma_distribucija.prizma.feature_track_location.domain.*
import com.prizma_distribucija.prizma.feature_track_location.domain.fakes.GoogleMapMangerFakeImpl
import com.prizma_distribucija.prizma.feature_track_location.domain.fakes.LocationTrackerFakeImplAndroidTest
import com.prizma_distribucija.prizma.feature_track_location.domain.fakes.PermissionManagerFakeImpl
import com.prizma_distribucija.prizma.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@MediumTest
@ExperimentalCoroutinesApi
@HiltAndroidTest
@UninstallModules(CoreModule::class)
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

        assert(LocationTrackerFakeImplAndroidTest._pathPoints.value.first() == LatLng(1.0, 1.0))

        assert(GoogleMapMangerFakeImpl.isOnNewPathPointsCalled == true)
    }

    @Test
    fun onFragmentLaunch_isMapsStyleSet()  {
        GoogleMapMangerFakeImpl.isSetStyleCalled = false
        launchFragmentInHiltContainer<TrackLocationFragment> {
            val supportMapFragment =
                childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment?
            supportMapFragment?.getMapAsync { googleMap ->
                assert(GoogleMapMangerFakeImpl.isSetStyleCalled == true)
            }
        }
    }
}