package com.astraloom.usecase

import com.astraloom.astronomy.AstronomyEngine
import com.astraloom.domain.Observer
import com.astraloom.domain.Star
import com.astraloom.repository.StarRepository
import kotlinx.datetime.Instant

/**
 * Use case for getting visible stars from observer location
 * (可視星取得ユースケース)
 *
 * This use case combines:
 * - Star data from repository
 * - Astronomy calculations from engine
 * - Visibility filtering
 */
class GetVisibleStarsUseCase(
    private val starRepository: StarRepository,
    private val astronomyEngine: AstronomyEngine
) {
    /**
     * Get all visible stars from observer location at given time
     *
     * @param observer Observer location
     * @param time Observation time
     * @param maxMagnitude Maximum magnitude to include (default: 4.0 for bright stars)
     * @return List of visible stars with their positions
     */
    suspend fun execute(
        observer: Observer,
        time: Instant,
        maxMagnitude: Double = 4.0
    ): Result<List<VisibleStar>> {
        return try {
            // Get bright stars from repository
            val starsResult = starRepository.getStarsByMagnitude(maxMagnitude)
            val stars = starsResult.getOrThrow()

            // Calculate positions for all stars
            val positions = astronomyEngine.calculateVisibleStars(stars, observer, time)

            // Convert to VisibleStar objects
            val visibleStars = positions.map { (starId, position) ->
                val star = stars.first { it.id == starId }
                VisibleStar(
                    star = star,
                    altitudeDegrees = position.altitudeDegrees(),
                    azimuthDegrees = position.azimuthDegrees(),
                    hourAngle = position.hourAngle
                )
            }.sortedByDescending { it.altitudeDegrees } // Sort by altitude (highest first)

            Result.success(visibleStars)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to get visible stars: ${e.message}", e))
        }
    }

    /**
     * Get only the brightest visible stars (magnitude < 2.0)
     */
    suspend fun getBrightestVisible(
        observer: Observer,
        time: Instant
    ): Result<List<VisibleStar>> {
        return execute(observer, time, maxMagnitude = 2.0)
    }
}

/**
 * Visible star with calculated position
 */
data class VisibleStar(
    val star: Star,
    val altitudeDegrees: Double,
    val azimuthDegrees: Double,
    val hourAngle: Double
)
