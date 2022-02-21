package com.prizma_distribucija.prizma.feature_track_location.domain.use_cases

import com.prizma_distribucija.prizma.core.util.Resource
import com.prizma_distribucija.prizma.feature_track_location.domain.TrackLocationRepository
import com.prizma_distribucija.prizma.feature_track_location.domain.model.Route
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SaveRouteToRemoteDatabaseUseCase @Inject constructor(
    private val trackLocationRepository: TrackLocationRepository,
) {

    suspend operator fun invoke(route: Route) = flow<Resource<Boolean>> {
        emit(Resource.Loading(false))

        val task = trackLocationRepository.saveRouteToRemoteDatabase(route)

        if (task.isSuccess) {
            emit(Resource.Success(true))
        } else {
            emit(Resource.Error(task.errorMessage ?: "Unknown error appeared", false))
        }
    }
}