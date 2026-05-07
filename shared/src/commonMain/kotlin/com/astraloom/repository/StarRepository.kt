package com.astraloom.repository

import com.astraloom.domain.Star

/**
 * Repository interface for star data access
 * (恒星データアクセスのリポジトリインターフェース)
 *
 * This interface abstracts the data source, allowing multiple implementations:
 * - Local data source (embedded JSON)
 * - Remote API data source
 * - Cache/database implementation
 */
interface StarRepository {

    /**
     * Get all stars in the catalog
     *
     * @return List of all stars
     */
    suspend fun getAllStars(): Result<List<Star>>

    /**
     * Get a star by its ID
     *
     * @param id Star identifier (e.g., HIP number, Bayer designation)
     * @return Star if found, null otherwise
     */
    suspend fun getStarById(id: String): Result<Star?>

    /**
     * Get stars by magnitude range (for filtering by brightness)
     *
     * @param maxMagnitude Maximum magnitude (lower = brighter)
     * @param minMagnitude Minimum magnitude (optional, default -30.0 to include all stars)
     * @return List of stars within magnitude range
     */
    suspend fun getStarsByMagnitude(
        maxMagnitude: Double,
        minMagnitude: Double = -30.0
    ): Result<List<Star>>

    /**
     * Get bright stars (magnitude < 4.0) suitable for naked eye observation
     *
     * @return List of bright stars
     */
    suspend fun getBrightStars(): Result<List<Star>>

    /**
     * Get stars by IDs (useful for constellation rendering)
     *
     * @param ids List of star IDs
     * @return List of stars matching the IDs
     */
    suspend fun getStarsByIds(ids: List<String>): Result<List<Star>>

    /**
     * Search stars by name
     *
     * @param query Search query (partial match)
     * @return List of stars matching the query
     */
    suspend fun searchStarsByName(query: String): Result<List<Star>>

    /**
     * Refresh/reload star data (if using remote source)
     */
    suspend fun refresh(): Result<Unit>
}
