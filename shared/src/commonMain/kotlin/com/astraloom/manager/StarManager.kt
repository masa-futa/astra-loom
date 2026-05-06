package com.astraloom.manager

import com.astraloom.domain.Observer
import com.astraloom.domain.Star
import com.astraloom.usecase.GetVisibleStarsUseCase
import com.astraloom.usecase.SearchStarsUseCase
import com.astraloom.usecase.VisibleStar
import kotlinx.datetime.Instant

/**
 * Star Manager - Facade for star-related operations
 * (星関連操作のファサード)
 *
 * This manager integrates multiple use cases related to stars,
 * providing a high-level API for platform-specific implementations.
 */
class StarManager(
    private val getVisibleStarsUseCase: GetVisibleStarsUseCase,
    private val searchStarsUseCase: SearchStarsUseCase
) {
    /**
     * Get all visible stars from observer location
     *
     * @param observer Observer location
     * @param time Observation time
     * @param maxMagnitude Maximum magnitude to include (default: 4.0)
     * @return List of visible stars with positions, sorted by altitude
     */
    suspend fun getVisibleStars(
        observer: Observer,
        time: Instant,
        maxMagnitude: Double = 4.0
    ): Result<List<VisibleStar>> {
        return getVisibleStarsUseCase.execute(observer, time, maxMagnitude)
    }

    /**
     * Get only the brightest visible stars (magnitude < 2.0)
     *
     * @param observer Observer location
     * @param time Observation time
     * @return List of brightest visible stars
     */
    suspend fun getBrightestStars(
        observer: Observer,
        time: Instant
    ): Result<List<VisibleStar>> {
        return getVisibleStarsUseCase.getBrightestVisible(observer, time)
    }

    /**
     * Search stars by name
     *
     * @param query Search query
     * @param observer Observer location (optional, for position calculation)
     * @param time Observation time (optional, for position calculation)
     * @return List of matching stars
     */
    suspend fun searchStars(
        query: String,
        observer: Observer? = null,
        time: Instant? = null
    ): Result<List<com.astraloom.usecase.SearchResult>> {
        return searchStarsUseCase.execute(query, observer, time)
    }

    /**
     * Get visible stars count
     *
     * @param observer Observer location
     * @param time Observation time
     * @param maxMagnitude Maximum magnitude
     * @return Number of visible stars
     */
    suspend fun getVisibleStarsCount(
        observer: Observer,
        time: Instant,
        maxMagnitude: Double = 4.0
    ): Result<Int> {
        return getVisibleStars(observer, time, maxMagnitude).map { it.size }
    }

    /**
     * Get stars above a specific altitude
     *
     * @param observer Observer location
     * @param time Observation time
     * @param minAltitudeDegrees Minimum altitude in degrees
     * @return List of stars above the specified altitude
     */
    suspend fun getStarsAboveAltitude(
        observer: Observer,
        time: Instant,
        minAltitudeDegrees: Double
    ): Result<List<VisibleStar>> {
        return getVisibleStars(observer, time).map { stars ->
            stars.filter { it.altitudeDegrees >= minAltitudeDegrees }
        }
    }
}
