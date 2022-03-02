package com.prizma_distribucija.prizma.feature_track_location.presentation.track_location

import android.app.NotificationManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.prizma_distribucija.prizma.R
import com.prizma_distribucija.prizma.core.di.SingletonModule
import com.prizma_distribucija.prizma.feature_track_location.domain.fakes.LocationTrackerFakeImplAndroidTest
import com.prizma_distribucija.prizma.feature_track_location.domain.fakes.PermissionManagerFakeImpl
import com.prizma_distribucija.prizma.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@HiltAndroidTest
@UninstallModules(SingletonModule::class)
class TrackingForegroundServiceTests {

    @get:Rule()
    var hiltRule = HiltAndroidRule(this)

    lateinit var context: Context

    @Before
    fun setUp() {
        hiltRule.inject()
        PermissionManagerFakeImpl.setDefaults()
        context = ApplicationProvider.getApplicationContext()
    }

    @After
    fun cleanUp() {
        LocationTrackerFakeImplAndroidTest.setDefaults()
    }

    @Test
    fun startTracking_NotificationIsDisplayed() {
        launchFragmentInHiltContainer<TrackLocationFragment> {
        }

        //should not be tracking
        assert(LocationTrackerFakeImplAndroidTest._isTrackingStateFlow.value == false)

        PermissionManagerFakeImpl.hasPermissions = true

        //start service
        //startStopBtnClick sends a command to service
        onView(withId(R.id.btn_stop_start)).perform(
            click()
        )

        assert(LocationTrackerFakeImplAndroidTest._isTrackingStateFlow.value == true)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notifications = notificationManager.activeNotifications

        assert(notifications.isNotEmpty())
    }

    @Test
    fun onStop_removeNotification() {
        launchFragmentInHiltContainer<TrackLocationFragment> {
        }

        //should not be tracking
        assert(LocationTrackerFakeImplAndroidTest._isTrackingStateFlow.value == false)

        PermissionManagerFakeImpl.hasPermissions = true

        //start service
        //startStopBtnClick sends a command to service
        onView(withId(R.id.btn_stop_start)).perform(
            click()
        )

        assert(LocationTrackerFakeImplAndroidTest._isTrackingStateFlow.value == true)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notifications = notificationManager.activeNotifications

        //assert there is notifications to remove
        assert(notifications.isNotEmpty())

        //stop service
        onView(withId(R.id.btn_stop_start)).perform(
            click()
        )

        val newNotifications = notificationManager.activeNotifications

        //assert that all notifications are removed
        assert(newNotifications.isEmpty())
    }

    @Test
    fun onStart_doesNotHavePermissions_doNotStartForegroundService() {
        launchFragmentInHiltContainer<TrackLocationFragment> {
        }

        assert(LocationTrackerFakeImplAndroidTest._isTrackingStateFlow.value == false)

        PermissionManagerFakeImpl.hasPermissions = false

        //start service
        onView(withId(R.id.btn_stop_start)).perform(
            click()
        )

        assert(LocationTrackerFakeImplAndroidTest._isTrackingStateFlow.value == false)
    }
}