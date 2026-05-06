package com.astraloom.data.repository

import com.astraloom.data.source.LocalStarDataSource
import com.astraloom.domain.Star
import com.astraloom.repository.StarRepository

/**
 * Local implementation of StarRepository using embedded JSON data
 */
class LocalStarRepository(
    private val dataSource: LocalStarDataSource
) : StarRepository {

    override suspend fun getAllStars(): Result<List<Star>> {
        return dataSource.loadStars()
    }

    override suspend fun getStarById(id: String): Result<Star?> {
        return getAllStars().map { stars ->
            stars.find { it.id == id }
        }
    }

    override suspend fun getStarsByMagnitude(
        maxMagnitude: Double,
        minMagnitude: Double
    ): Result<List<Star>> {
        return getAllStars().map { stars ->
            stars.filter { star ->
                star.magnitude <= maxMagnitude && star.magnitude >= minMagnitude
            }
        }
    }

    override suspend fun getBrightStars(): Result<List<Star>> {
        return getStarsByMagnitude(maxMagnitude = 4.0)
    }

    override suspend fun getStarsByIds(ids: List<String>): Result<List<Star>> {
        return getAllStars().map { stars ->
            stars.filter { it.id in ids }
        }
    }

    override suspend fun searchStarsByName(query: String): Result<List<Star>> {
        return getAllStars().map { stars ->
            stars.filter { star ->
                star.name?.contains(query, ignoreCase = true) == true
            }
        }
    }

    override suspend fun refresh(): Result<Unit> {
        return try {
            dataSource.clearCache()
            dataSource.loadStars().map { }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
