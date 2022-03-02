package com.prizma_distribucija.prizma.feature_track_location.domain.model

import com.google.common.truth.Truth.assertThat
import com.prizma_distribucija.prizma.core.util.EntityMapper
import com.prizma_distribucija.prizma.feature_track_location.data.remote.dto.RouteDto
import org.junit.Before
import org.junit.Test

class RouteDtoMapperTest {

    private lateinit var mapper: EntityMapper<RouteDto, Route>

    @Before
    fun setUp() {
        mapper = RouteDtoMapper()
    }

    @Test
    fun mapToDto_correctlyMaps() {
        val domainObject = Route(
            avgSpeed = "1.0",
            distanceTravelled = "1.0",
            month = 1,
            pathPoints = emptyList(),
            timeFinished = "01:00",
            timeStarted = "00:59",
            userId = "userId",
            year = 2022,
            day = 2
        )

        val expectedDto = RouteDto(
            avgSpeed = "1.0",
            distanceTravelled = "1.0",
            month = 1,
            pathPoints = emptyList(),
            timeFinished = "01:00",
            timeStarted = "00:59",
            userId = "userId",
            year = 2022,
            day = 2
        )

        val actualDto = mapper.mapToDto(domainObject)

        assertThat(actualDto).isEqualTo(expectedDto)
    }

    @Test
    fun mapFromDto_correctlyMaps() {
        val dto = RouteDto(
            avgSpeed = "1.0",
            distanceTravelled = "1.0",
            month = 1,
            pathPoints = emptyList(),
            timeFinished = "01:00",
            timeStarted = "00:59",
            userId = "userId",
            year = 2022,
            day = 2
        )

        val expectedDomainObject = Route (
            avgSpeed = "1.0",
            distanceTravelled = "1.0",
            month = 1,
            pathPoints = emptyList(),
            timeFinished = "01:00",
            timeStarted = "00:59",
            userId = "userId",
            year = 2022,
            day = 2
        )

        val actualDomainObject = mapper.mapFromDto(dto)

        assertThat(actualDomainObject).isEqualTo(expectedDomainObject)
    }
}