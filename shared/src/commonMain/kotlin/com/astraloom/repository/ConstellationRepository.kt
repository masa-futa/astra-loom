package com.astraloom.repository

import com.astraloom.domain.Constellation
import com.astraloom.domain.Star

/**
 * Repository interface for constellation data access
 * (星座データアクセスのリポジトリインターフェース)
 *
 * This interface abstracts constellation data access, allowing multiple implementations.
 */
interface ConstellationRepository {

    /**
     * Get all constellations
     *
     * @return List of all constellations
     */
    suspend fun getAllConstellations(): Result<List<Constellation>>

    /**
     * Get a constellation by its ID (IAU abbreviation)
     *
     * @param id Constellation ID (e.g., "Ori" for Orion)
     * @return Constellation if found, null otherwise
     */
    suspend fun getConstellationById(id: String): Result<Constellation?>

    /**
     * Get a constellation by name
     *
     * @param name Constellation name (e.g., "Orion")
     * @return Constellation if found, null otherwise
     */
    suspend fun getConstellationByName(name: String): Result<Constellation?>

    /**
     * Get stars that belong to a specific constellation
     *
     * Requires StarRepository to resolve star IDs
     *
     * @param constellationId Constellation ID
     * @return List of stars in the constellation
     */
    suspend fun getStarsInConstellation(constellationId: String): Result<List<Star>>

    /**
     * Get constellation lines (pairs of star IDs)
     *
     * @param constellationId Constellation ID
     * @return List of line segments (star ID pairs)
     */
    suspend fun getConstellationLines(constellationId: String): Result<List<Pair<String, String>>>

    /**
     * Search constellations by name
     *
     * @param query Search query (partial match)
     * @return List of constellations matching the query
     */
    suspend fun searchConstellationsByName(query: String): Result<List<Constellation>>

    /**
     * Get major constellations (for initial display)
     *
     * Returns a curated list of well-known constellations
     *
     * @return List of major constellations
     */
    suspend fun getMajorConstellations(): Result<List<Constellation>>

    /**
     * Refresh/reload constellation data
     */
    suspend fun refresh(): Result<Unit>
}
