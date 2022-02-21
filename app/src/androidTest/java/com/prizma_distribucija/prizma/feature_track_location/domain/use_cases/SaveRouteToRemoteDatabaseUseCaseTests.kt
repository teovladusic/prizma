package com.prizma_distribucija.prizma.feature_track_location.domain.use_cases

import com.prizma_distribucija.prizma.core.util.Resource
import com.prizma_distribucija.prizma.feature_track_location.domain.TrackLocationRepository
import com.prizma_distribucija.prizma.feature_track_location.domain.model.Route
import com.prizma_distribucija.prizma.feature_track_location.domain.model.TaskResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@ExperimentalCoroutinesApi
class SaveRouteToRemoteDatabaseUseCaseTests {

    @Test
    fun invoke_shouldEmitLoading() = runTest {
        val trackLocationRepository = mock(TrackLocationRepository::class.java)

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

        `when`(trackLocationRepository.saveRouteToRemoteDatabase(route))
            .thenReturn(TaskResult(isComplete = true, isSuccess = true, errorMessage = null))

        val useCase = SaveRouteToRemoteDatabaseUseCase(trackLocationRepository)

        val results = useCase(route).toList()

        assert(results.first()::class == Resource.Loading::class)
    }

    @Test
    fun invoke_shouldEmitLoadingThenSuccess() = runTest {
        val trackLocationRepository = mock(TrackLocationRepository::class.java)

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

        `when`(trackLocationRepository.saveRouteToRemoteDatabase(route))
            .thenReturn(TaskResult(isComplete = true, isSuccess = true, errorMessage = null))

        val useCase = SaveRouteToRemoteDatabaseUseCase(trackLocationRepository)

        val results = useCase(route).toList()

        assert(results.first()::class == Resource.Loading::class)
        assert(results[1]::class == Resource.Success::class)
    }

    @Test
    fun invoke_shouldEmitLoadingThenError() = runTest {
        val trackLocationRepository = mock(TrackLocationRepository::class.java)

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

        `when`(trackLocationRepository.saveRouteToRemoteDatabase(route))
            .thenReturn(TaskResult(isComplete = true, isSuccess = false, errorMessage = null))

        val useCase = SaveRouteToRemoteDatabaseUseCase(trackLocationRepository)

        val results = useCase(route).toList()

        assert(results.first()::class == Resource.Loading::class)
        assert(results[1]::class == Resource.Error::class)
    }
}