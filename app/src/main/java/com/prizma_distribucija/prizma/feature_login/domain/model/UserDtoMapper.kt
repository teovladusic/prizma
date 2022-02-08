package com.prizma_distribucija.prizma.feature_login.domain.model

import com.prizma_distribucija.prizma.core.domain.model.User
import com.prizma_distribucija.prizma.core.util.EntityMapper
import com.prizma_distribucija.prizma.feature_login.data.remote.dto.UserDto

class UserDtoMapper : EntityMapper<UserDto, User> {
    override fun mapFromDto(dto: UserDto): User {
        return User(
            code = dto.code,
            lastName = dto.lastName,
            name = dto.name,
            userId = dto.userId
        )
    }

    override fun mapToDto(domainModel: User): UserDto {
        return UserDto(
            code = domainModel.code,
            lastName = domainModel.lastName,
            name = domainModel.name,
            userId = domainModel.userId
        )
    }
}