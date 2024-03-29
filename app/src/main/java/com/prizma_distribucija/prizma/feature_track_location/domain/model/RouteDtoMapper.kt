package com.prizma_distribucija.prizma.feature_track_location.domain.model

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.prizma_distribucija.prizma.core.util.EntityMapper
import com.prizma_distribucija.prizma.feature_track_location.data.remote.dto.RouteDto

class RouteDtoMapper : EntityMapper<RouteDto, Route> {

    override fun mapFromDto(dto: RouteDto): Route {
        return Route(
            avgSpeed = dto.avgSpeed,
            distanceTravelled = dto.distanceTravelled,
            month = dto.month,
            pathPoints = emptyList(),
            timeFinished = dto.timeFinished,
            timeStarted = dto.timeStarted,
            userId = dto.userId,
            year = dto.year,
            day = dto.day
        )
    }

    override fun mapToDto(domainModel: Route): RouteDto {
        return RouteDto(
            avgSpeed = domainModel.avgSpeed,
            distanceTravelled = domainModel.distanceTravelled,
            month = domainModel.month,
            pathPointIds = emptyList(),
            timeFinished = domainModel.timeFinished,
            timeStarted = domainModel.timeStarted,
            userId = domainModel.userId,
            year = domainModel.year,
            day = domainModel.day
        )
    }
}