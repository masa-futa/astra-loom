package com.astraloom.api.repository

import com.astraloom.api.source.RemoteStarDataSource
import com.astraloom.domain.Star
import com.astraloom.repository.StarRepository

/**
 * Remote implementation of StarRepository using API
 * (APIを使用したリモートStarRepository実装)
 */
class RemoteStarRepository(
    private val dataSource: RemoteStarDataSource
) : StarRepository {

    override suspend fun getAllStars(): Result<List<Star>> {
        return dataSource.fetchStars()
    }

    override suspend fun getStarById(id: String): Result<Star?> {
        return dataSource.fetchStarById(id)
    }

    override suspend fun getStarsByMagnitude(
        maxMagnitude: Double,
        minMagnitude: Double
    ): Result<List<Star>> {
        // Fetch all stars and filter by magnitude
        // In real API, this would be done server-side
        return getAllStars().map { stars ->
            stars.filter { star ->
                star.magnitude <= maxMagnitude && star.magnitude >= minMagnitude
            }
        }
    }

    override suspend fun getBrightStars(): Result<List<Star>> {
        return dataSource.fetchBrightStars()
    }

    override suspend fun getStarsByIds(ids: List<String>): Result<List<Star>> {
        // Fetch all stars and filter by IDs
        // In real API, this could be optimized with a bulk query
        return getAllStars().map { stars ->
            stars.filter { it.id in ids }
        }
    }

    override suspend fun searchStarsByName(query: String): Result<List<Star>> {
        return dataSource.searchStars(query)
    }

    override suspend fun refresh(): Result<Unit> {
        // For remote repository, refresh means fetching fresh data
        return getAllStars().map { }
    }
}
