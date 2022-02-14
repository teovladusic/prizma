package com.prizma_distribucija.prizma.feature_track_location.domain.use_cases

import android.net.Uri
import com.prizma_distribucija.prizma.core.util.Resource
import com.prizma_distribucija.prizma.feature_track_location.domain.TrackLocationRepository
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SaveUriToRemoteDatabaseUseCase @Inject constructor(
    private val trackLocationRepository: TrackLocationRepository
) {

    suspend operator fun invoke(path: String, uri: Uri) = flow {
        emit(Resource.Loading(false))

        val taskResult = trackLocationRepository.saveBitmapToRemoteDatabase(uri, path)

        if (taskResult.isSuccess) {
            emit(Resource.Success(true))
        } else {
            emit(Resource.Error(taskResult.errorMessage ?: "Unknown error appeared", false))
        }
    }
}
