package com.astraloom.manager

import com.astraloom.domain.Observer
import com.astraloom.usecase.ConstellationWithStars
import com.astraloom.usecase.GetConstellationStarsUseCase
import kotlinx.datetime.Instant

/**
 * Constellation Manager - Facade for constellation-related operations
 * (星座関連操作のファサード)
 *
 * This manager integrates constellation-related use cases,
 * providing a high-level API for platform-specific implementations.
 */
class ConstellationManager(
    private val getConstellationStarsUseCase: GetConstellationStarsUseCase
) {
    /**
     * Get a constellation with its stars and positions
     *
     * @param constellationId Constellation ID (e.g., "Ori" for Orion)
     * @param observer Observer location
     * @param time Observation time
     * @return Constellation with positioned stars
     */
    suspend fun getConstellation(
        constellationId: String,
        observer: Observer,
        time: Instant
    ): Result<ConstellationWithStars> {
        return getConstellationStarsUseCase.execute(constellationId, observer, time)
    }

    /**
     * Get all major constellations
     *
     * @param observer Observer location
     * @param time Observation time
     * @return List of major constellations with their stars
     */
    suspend fun getMajorConstellations(
        observer: Observer,
        time: Instant
    ): Result<List<ConstellationWithStars>> {
        return getConstellationStarsUseCase.getMajorConstellations(observer, time)
    }

    /**
     * Get only visible constellations (at least one star above horizon)
     *
     * @param observer Observer location
     * @param time Observation time
     * @return List of visible constellations
     */
    suspend fun getVisibleConstellations(
        observer: Observer,
        time: Instant
    ): Result<List<ConstellationWithStars>> {
        return getMajorConstellations(observer, time).map { constellations ->
            constellations.filter { constellation ->
                constellation.stars.any { it.isVisible }
            }
        }
    }

    /**
     * Check if a constellation is currently visible
     *
     * @param constellationId Constellation ID
     * @param observer Observer location
     * @param time Observation time
     * @return true if at least one star is visible
     */
    suspend fun isConstellationVisible(
        constellationId: String,
        observer: Observer,
        time: Instant
    ): Result<Boolean> {
        return getConstellation(constellationId, observer, time).map { constellation ->
            constellation.stars.any { it.isVisible }
        }
    }

    /**
     * Get constellation visibility percentage
     *
     * @param constellationId Constellation ID
     * @param observer Observer location
     * @param time Observation time
     * @return Percentage of visible stars (0.0 - 1.0)
     */
    suspend fun getConstellationVisibility(
        constellationId: String,
        observer: Observer,
        time: Instant
    ): Result<Double> {
        return getConstellation(constellationId, observer, time).map { constellation ->
            if (constellation.stars.isEmpty()) {
                0.0
            } else {
                val visibleCount = constellation.stars.count { it.isVisible }
                visibleCount.toDouble() / constellation.stars.size
            }
        }
    }
}
