package com.prizma_distribucija.prizma.feature_track_location.domain.data

import androidx.core.net.toUri
import com.prizma_distribucija.prizma.feature_track_location.domain.TrackLocationRepository
import com.prizma_distribucija.prizma.feature_track_location.domain.fakes.TrackLocationRepositoryFakeImpl
import com.prizma_distribucija.prizma.feature_track_location.domain.model.TaskResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

@ExperimentalCoroutinesApi
class TrackLocationRepositoryTests {

    @Test
    fun saveBitmap_shouldReturnCorrectResults() = runTest {
        val repo = TrackLocationRepositoryFakeImpl() as TrackLocationRepository

        val uri = "".toUri()
        val path = ""
        val result = repo.saveBitmapToRemoteDatabase(uri, path)
        val expectedResult = TaskResult(isComplete = true, isSuccess = false, errorMessage = null)
        assert(result == expectedResult)
    }
}