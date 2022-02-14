package com.prizma_distribucija.prizma.feature_track_location.domain.use_cases

import androidx.core.net.toUri
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.prizma_distribucija.prizma.core.util.Resource
import com.prizma_distribucija.prizma.feature_track_location.domain.TrackLocationRepository
import com.prizma_distribucija.prizma.feature_track_location.domain.model.TaskResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@ExperimentalCoroutinesApi
class SaveUriToRemoteDatabaseUseCaseTests {

    @Test
    fun invoke_emitsLoading() = runTest {
        val trackLocationRepository = mock(TrackLocationRepository::class.java)

        val uri = "".toUri()
        val path = ""
        `when`(trackLocationRepository.saveBitmapToRemoteDatabase(uri, path)).thenReturn(
            TaskResult(
                isSuccess = false,
                isComplete = false,
                errorMessage = null
            )
        )

        val useCase = SaveUriToRemoteDatabaseUseCase(trackLocationRepository)

        val results = useCase(path, uri).toList()

        assert(results[0]::class == Resource.Loading::class)
        assert(results[0].data == false)
    }

    @Test
    fun invoke_emitsLoadingAndError() = runTest {
        val trackLocationRepository = mock(TrackLocationRepository::class.java)

        val uri = "".toUri()
        val path = ""
        `when`(trackLocationRepository.saveBitmapToRemoteDatabase(uri, path)).thenReturn(
            TaskResult(
                isSuccess = false,
                isComplete = true,
                errorMessage = null
            )
        )

        val useCase = SaveUriToRemoteDatabaseUseCase(trackLocationRepository)

        val results = useCase(path, uri).toList()

        assert(results[1]::class == Resource.Error::class)
        assert(results[1].data == false)
        assert(results[1].message == "Unknown error appeared")
    }

    @Test
    fun invoke_emitsLoadingAndErrorWithErrorMessage() = runTest {
        val trackLocationRepository = mock(TrackLocationRepository::class.java)

        val uri = "".toUri()
        val path = ""
        val errorMessage = "Random error message"
        `when`(trackLocationRepository.saveBitmapToRemoteDatabase(uri, path)).thenReturn(
            TaskResult(
                isSuccess = false,
                isComplete = true,
                errorMessage = errorMessage
            )
        )

        val useCase = SaveUriToRemoteDatabaseUseCase(trackLocationRepository)

        val results = useCase(path, uri).toList()

        assert(results[1]::class == Resource.Error::class)
        assert(results[1].data == false)
        assert(results[1].message == errorMessage)
    }

    @Test
    fun invoke_emitsLoadingAndSuccess() = runTest {
        val trackLocationRepository = mock(TrackLocationRepository::class.java)

        val uri = "".toUri()
        val path = ""

        `when`(trackLocationRepository.saveBitmapToRemoteDatabase(uri, path)).thenReturn(
            TaskResult(
                isSuccess = true,
                isComplete = true,
                errorMessage = null
            )
        )

        val useCase = SaveUriToRemoteDatabaseUseCase(trackLocationRepository)

        val results = useCase(path, uri).toList()

        assert(results[1]::class == Resource.Success::class)
        assert(results[1].data == true)
    }
}