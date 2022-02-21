package com.prizma_distribucija.prizma.feature_track_location.data.repository

import android.annotation.TargetApi
import android.net.Uri
import com.prizma_distribucija.prizma.core.data.services.FirebaseService
import com.prizma_distribucija.prizma.core.util.DispatcherProvider
import com.prizma_distribucija.prizma.core.util.EntityMapper
import com.prizma_distribucija.prizma.feature_track_location.data.remote.dto.RouteDto
import com.prizma_distribucija.prizma.feature_track_location.domain.TrackLocationRepository
import com.prizma_distribucija.prizma.feature_track_location.domain.model.Route
import com.prizma_distribucija.prizma.feature_track_location.domain.model.RouteDtoMapper
import com.prizma_distribucija.prizma.feature_track_location.domain.model.TaskResult
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import javax.inject.Inject

class TrackLocationRepositoryImpl @Inject constructor(
    private val firebaseService: FirebaseService,
    private val mapper: EntityMapper<RouteDto, Route>,
    private val dispatchers: DispatcherProvider
) : TrackLocationRepository {

    override suspend fun saveRouteToRemoteDatabase(route: Route): TaskResult {
        return withContext(dispatchers.io) {
            val routeDto = mapper.mapToDto(route)
            firebaseService.saveRouteInFirebaseFirestore(routeDto)
        }
    }
}