package com.prizma_distribucija.prizma.feature_track_location.domain.use_cases

import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.MediumTest
import com.prizma_distribucija.prizma.core.util.Constants
import com.prizma_distribucija.prizma.feature_track_location.domain.use_cases.BuildNotificationUseCase
import org.junit.Test

@MediumTest
class BuildNotificationUseCaseTests {

    @Test
    fun getNotificationBuilder_returnsCorrectBuilder() {
        val notificationManager = BuildNotificationUseCase(
            context =
            ApplicationProvider.getApplicationContext()
        )

        val builder = notificationManager()

        val notification = builder.build()

        assert(notification.channelId == Constants.NOTIFICATION_CHANNEL_ID)
    }
}