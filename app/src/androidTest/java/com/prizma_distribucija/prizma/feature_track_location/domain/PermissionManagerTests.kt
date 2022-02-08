package com.prizma_distribucija.prizma.feature_track_location.domain

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.*
import pub.devrel.easypermissions.EasyPermissions

@MediumTest
class PermissionManagerTests {
    //cannot mock EasyPermissions static methods
}