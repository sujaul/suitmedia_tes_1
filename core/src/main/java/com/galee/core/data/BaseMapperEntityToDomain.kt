package com.galee.core.data

interface BaseMapperEntityToDomain<E, D> {
    fun mapDomainToEntity(domain: D): E
    fun mapEntityToDomain(entity: E): D

    fun mapEntitysToListDomain(entitys: List<E>): List<D> {
        val listDomain = mutableListOf<D>()
        entitys.map { listDomain.add(mapEntityToDomain(it)) }
        return listDomain
    }

    fun mapDomainsToListEntity(domains: List<D>): List<E> {
        val listEntity = mutableListOf<E>()
        domains.map { listEntity.add(mapDomainToEntity(it)) }
        return listEntity
    }
}