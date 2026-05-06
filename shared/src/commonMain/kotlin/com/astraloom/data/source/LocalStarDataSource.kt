package com.astraloom.data.source

import com.astraloom.data.model.StarCatalogDto
import com.astraloom.domain.Star
import kotlinx.serialization.json.Json

/**
 * Local data source for stars (embedded JSON)
 * (ローカル星データソース)
 */
class LocalStarDataSource(
    private val resourceReader: ResourceReader
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private var cachedStars: List<Star>? = null

    /**
     * Load all stars from embedded JSON
     */
    suspend fun loadStars(): Result<List<Star>> {
        return try {
            // Return cached data if available
            cachedStars?.let { return Result.success(it) }

            // Load JSON from resources
            val jsonString = resourceReader.readResource("stars.json")

            // Parse JSON
            val catalog = json.decodeFromString<StarCatalogDto>(jsonString)

            // Convert to domain models
            val stars = catalog.stars.map { it.toDomain() }

            // Cache for future use
            cachedStars = stars

            Result.success(stars)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to load stars: ${e.message}", e))
        }
    }

    /**
     * Clear cache (useful for refresh)
     */
    fun clearCache() {
        cachedStars = null
    }
}
