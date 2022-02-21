package com.prizma_distribucija.prizma.feature_track_location.domain.data

import androidx.core.net.toUri
import com.prizma_distribucija.prizma.feature_track_location.domain.TrackLocationRepository
import com.prizma_distribucija.prizma.feature_track_location.domain.fakes.TrackLocationRepositoryFakeImpl
import com.prizma_distribucija.prizma.feature_track_location.domain.model.Route
import com.prizma_distribucija.prizma.feature_track_location.domain.model.TaskResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

@ExperimentalCoroutinesApi
class TrackLocationRepositoryTests {

    @Test
    fun saveRoute_shouldReturnCorrectResults() = runTest {
        val repo = TrackLocationRepositoryFakeImpl() as TrackLocationRepository

        val route = Route(
            "",
            "",
            1,
            emptyList(),
            "",
            "",
            "",
            1,
            1
        )
        val result = repo.saveRouteToRemoteDatabase(route)
        val expectedResult = TaskResult(isComplete = true, isSuccess = true, errorMessage = null)
        assert(result == expectedResult)
    }
}