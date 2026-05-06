package com.astraloom.data.source

import com.astraloom.data.model.ConstellationCatalogDto
import com.astraloom.domain.Constellation
import kotlinx.serialization.json.Json

/**
 * Local data source for constellations (embedded JSON)
 * (ローカル星座データソース)
 */
class LocalConstellationDataSource(
    private val resourceReader: ResourceReader
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private var cachedConstellations: List<Constellation>? = null

    /**
     * Load all constellations from embedded JSON
     */
    suspend fun loadConstellations(): Result<List<Constellation>> {
        return try {
            // Return cached data if available
            cachedConstellations?.let { return Result.success(it) }

            // Load JSON from resources
            val jsonString = resourceReader.readResource("constellations.json")

            // Parse JSON
            val catalog = json.decodeFromString<ConstellationCatalogDto>(jsonString)

            // Convert to domain models
            val constellations = catalog.constellations.map { it.toDomain() }

            // Cache for future use
            cachedConstellations = constellations

            Result.success(constellations)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to load constellations: ${e.message}", e))
        }
    }

    /**
     * Clear cache (useful for refresh)
     */
    fun clearCache() {
        cachedConstellations = null
    }
}
