package com.prizma_distribucija.prizma.feature_track_location.data.repository

import com.google.firebase.firestore.GeoPoint
import com.prizma_distribucija.prizma.core.data.services.FirebaseService
import com.prizma_distribucija.prizma.core.util.DispatcherProvider
import com.prizma_distribucija.prizma.core.util.EntityMapper
import com.prizma_distribucija.prizma.feature_track_location.data.remote.dto.PathPointDto
import com.prizma_distribucija.prizma.feature_track_location.data.remote.dto.RouteDto
import com.prizma_distribucija.prizma.feature_track_location.domain.TrackLocationRepository
import com.prizma_distribucija.prizma.feature_track_location.domain.model.Route
import com.prizma_distribucija.prizma.feature_track_location.domain.model.TaskResult
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.roundToInt

class TrackLocationRepositoryImpl @Inject constructor(
    private val firebaseService: FirebaseService,
    private val mapper: EntityMapper<RouteDto, Route>,
    private val dispatchers: DispatcherProvider
) : TrackLocationRepository {

    override suspend fun saveRouteToRemoteDatabase(route: Route): TaskResult {
        return withContext(dispatchers.io) {
            val numberOfItemsInList = 200

            val routePathPoints = route.pathPoints.map { GeoPoint(it.latitude, it.longitude) }

            val routeDto = mapper.mapToDto(route)

            val numberOfLists = (routePathPoints.size / numberOfItemsInList) + 1

            val pathPointIds = mutableListOf<String>()

            val pathPoints = mutableListOf<PathPointDto>()

            for (i in 0 until numberOfLists) {
                val id = firebaseService.createNewDocumentId()

                val from = numberOfItemsInList * i
                var to = from + numberOfItemsInList

                if (to > routePathPoints.size) {
                    to = routePathPoints.size
                }

                val pathPoint = PathPointDto(id, routePathPoints.subList(from, to), i)
                pathPoints.add(pathPoint)

                pathPointIds.add(id)
            }

            val newRoute = routeDto.copy(pathPointIds = pathPointIds)

            firebaseService.saveAllPathPointsInFirestore(pathPoints)

            firebaseService.saveRouteInFirebaseFirestore(newRoute)
        }
    }
}