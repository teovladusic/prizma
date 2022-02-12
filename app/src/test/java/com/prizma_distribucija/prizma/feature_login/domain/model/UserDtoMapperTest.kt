package com.prizma_distribucija.prizma.feature_login.domain.model

import com.prizma_distribucija.prizma.core.domain.model.User
import com.prizma_distribucija.prizma.feature_login.data.remote.dto.UserDto
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UserDtoMapperTest {

    private lateinit var mapper: UserDtoMapper

    @Before
    fun setUp() {
        mapper = UserDtoMapper()
    }

    @Test
    fun `map user dto to user domain model, passing the right data`() {
        val userDto =
            UserDto(code = "1234", lastName = "LastName", name = "Name", userId = "randomUserId")
        val expectedDomainObject =
            User(code = "1234", lastName = "LastName", name = "Name", userId = "randomUserId")

        val domainUser = mapper.mapFromDto(userDto)

        assertEquals(expectedDomainObject.code, domainUser.code)
        assertEquals(expectedDomainObject.lastName, domainUser.lastName)
        assertEquals(expectedDomainObject.name, domainUser.name)
        assertEquals(expectedDomainObject.userId, domainUser.userId)
    }

    @Test
    fun `map user domain model to dto, passing the right data`() {
        val userDomainModel =
            User(code = "1234", lastName = "LastName", name = "Name", userId = "randomUserId")
        val expectedDto =
            UserDto(code = "1234", lastName = "LastName", name = "Name", userId = "randomUserId")

        val dtoModel = mapper.mapToDto(userDomainModel)

        assertEquals(expectedDto.code, dtoModel.code)
        assertEquals(expectedDto.lastName, dtoModel.lastName)
        assertEquals(expectedDto.name, dtoModel.name)
        assertEquals(expectedDto.userId, dtoModel.userId)
    }
}