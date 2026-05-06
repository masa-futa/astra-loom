package com.astraloom.api.repository

import com.astraloom.api.source.RemoteConstellationDataSource
import com.astraloom.domain.Constellation
import com.astraloom.domain.Star
import com.astraloom.repository.ConstellationRepository
import com.astraloom.repository.StarRepository

/**
 * Remote implementation of ConstellationRepository using API
 * (APIを使用したリモートConstellationRepository実装)
 */
class RemoteConstellationRepository(
    private val dataSource: RemoteConstellationDataSource,
    private val starRepository: StarRepository
) : ConstellationRepository {

    override suspend fun getAllConstellations(): Result<List<Constellation>> {
        return dataSource.fetchConstellations()
    }

    override suspend fun getConstellationById(id: String): Result<Constellation?> {
        return dataSource.fetchConstellationById(id)
    }

    override suspend fun getConstellationByName(name: String): Result<Constellation?> {
        return getAllConstellations().map { constellations ->
            constellations.find { it.name.equals(name, ignoreCase = true) }
        }
    }

    override suspend fun getStarsInConstellation(constellationId: String): Result<List<Star>> {
        return getConstellationById(constellationId).mapCatching { constellation ->
            if (constellation == null) {
                emptyList()
            } else {
                starRepository.getStarsByIds(constellation.starIds)
                    .getOrThrow()
            }
        }
    }

    override suspend fun getConstellationLines(constellationId: String): Result<List<Pair<String, String>>> {
        return getConstellationById(constellationId).map { constellation ->
            constellation?.lines ?: emptyList()
        }
    }

    override suspend fun searchConstellationsByName(query: String): Result<List<Constellation>> {
        return dataSource.searchConstellations(query)
    }

    override suspend fun getMajorConstellations(): Result<List<Constellation>> {
        return dataSource.fetchMajorConstellations()
    }

    override suspend fun refresh(): Result<Unit> {
        return getAllConstellations().map { }
    }
}
