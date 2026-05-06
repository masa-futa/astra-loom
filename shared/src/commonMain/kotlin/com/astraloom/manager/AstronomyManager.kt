package com.astraloom.manager

import com.astraloom.astronomy.AstronomyEngine
import com.astraloom.domain.Observer
import com.astraloom.domain.Star
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Astronomy Manager - Facade for astronomy calculations
 * (天文計算のファサード)
 *
 * This manager provides high-level astronomy calculation APIs,
 * wrapping the AstronomyEngine for easier use from platform code.
 */
class AstronomyManager(
    private val astronomyEngine: AstronomyEngine
) {
    /**
     * Calculate star position at current time
     *
     * @param star Star to calculate position for
     * @param observer Observer location
     * @return Star position
     */
    suspend fun calculateStarPosition(
        star: Star,
        observer: Observer
    ): Result<AstronomyEngine.StarPosition> {
        return calculateStarPosition(star, observer, Clock.System.now())
    }

    /**
     * Calculate star position at specific time
     *
     * @param star Star to calculate position for
     * @param observer Observer location
     * @param time Observation time
     * @return Star position
     */
    suspend fun calculateStarPosition(
        star: Star,
        observer: Observer,
        time: Instant
    ): Result<AstronomyEngine.StarPosition> {
        return try {
            val position = astronomyEngine.calculateStarPosition(star, observer, time)
            Result.success(position)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to calculate star position: ${e.message}", e))
        }
    }

    /**
     * Calculate positions for multiple stars
     *
     * @param stars List of stars
     * @param observer Observer location
     * @param time Observation time
     * @return Map of star ID to position
     */
    suspend fun calculateStarPositions(
        stars: List<Star>,
        observer: Observer,
        time: Instant
    ): Result<Map<String, AstronomyEngine.StarPosition>> {
        return try {
            val positions = astronomyEngine.calculateStarPositions(stars, observer, time)
            Result.success(positions)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to calculate star positions: ${e.message}", e))
        }
    }

    /**
     * Calculate angular separation between two stars
     *
     * @param star1 First star
     * @param star2 Second star
     * @param time Observation time (for precession)
     * @return Angular separation in degrees
     */
    suspend fun calculateAngularSeparation(
        star1: Star,
        star2: Star,
        time: Instant
    ): Result<Double> {
        return try {
            val jd = com.astraloom.astronomy.JulianDate.fromInstant(time)
            val separationRadians = astronomyEngine.calculateAngularSeparation(star1, star2, jd)
            Result.success(Math.toDegrees(separationRadians))
        } catch (e: Exception) {
            Result.failure(Exception("Failed to calculate angular separation: ${e.message}", e))
        }
    }

    /**
     * Check if a star is visible (above horizon)
     *
     * @param star Star to check
     * @param observer Observer location
     * @param time Observation time
     * @return true if star is visible
     */
    suspend fun isStarVisible(
        star: Star,
        observer: Observer,
        time: Instant
    ): Result<Boolean> {
        return calculateStarPosition(star, observer, time).map { it.isVisible() }
    }
}
