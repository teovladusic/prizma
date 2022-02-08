package com.prizma_distribucija.prizma.core.util

interface EntityMapper<Dto, DomainModel> {

    fun mapFromDto(dto: Dto): DomainModel
    fun mapToDto(domainModel: DomainModel): Dto
}