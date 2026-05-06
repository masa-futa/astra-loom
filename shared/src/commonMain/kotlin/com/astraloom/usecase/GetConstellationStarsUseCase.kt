package com.astraloom.usecase

import com.astraloom.astronomy.AstronomyEngine
import com.astraloom.domain.Constellation
import com.astraloom.domain.Observer
import com.astraloom.domain.Star
import com.astraloom.repository.ConstellationRepository
import kotlinx.datetime.Instant

/**
 * Use case for getting constellation stars with their positions
 * (星座の星取得ユースケース)
 */
class GetConstellationStarsUseCase(
    private val constellationRepository: ConstellationRepository,
    private val astronomyEngine: AstronomyEngine
) {
    /**
     * Get a constellation with star positions
     *
     * @param constellationId Constellation ID (e.g., "Ori")
     * @param observer Observer location
     * @param time Observation time
     * @return Constellation with positioned stars
     */
    suspend fun execute(
        constellationId: String,
        observer: Observer,
        time: Instant
    ): Result<ConstellationWithStars> {
        return try {
            // Get constellation
            val constellationResult = constellationRepository.getConstellationById(constellationId)
            val constellation = constellationResult.getOrThrow()
                ?: return Result.failure(Exception("Constellation not found: $constellationId"))

            // Get stars in constellation
            val starsResult = constellationRepository.getStarsInConstellation(constellationId)
            val stars = starsResult.getOrThrow()

            // Calculate star positions
            val positions = astronomyEngine.calculateStarPositions(stars, observer, time)

            // Create positioned stars
            val positionedStars = stars.mapNotNull { star ->
                positions[star.id]?.let { position ->
                    PositionedStar(
                        star = star,
                        altitudeDegrees = position.altitudeDegrees(),
                        azimuthDegrees = position.azimuthDegrees(),
                        isVisible = position.isVisible()
                    )
                }
            }

            Result.success(
                ConstellationWithStars(
                    constellation = constellation,
                    stars = positionedStars,
                    lines = constellation.lines
                )
            )
        } catch (e: Exception) {
            Result.failure(Exception("Failed to get constellation stars: ${e.message}", e))
        }
    }

    /**
     * Get all major constellations with their stars
     */
    suspend fun getMajorConstellations(
        observer: Observer,
        time: Instant
    ): Result<List<ConstellationWithStars>> {
        return try {
            val constellationsResult = constellationRepository.getMajorConstellations()
            val constellations = constellationsResult.getOrThrow()

            val results = constellations.mapNotNull { constellation ->
                execute(constellation.id, observer, time).getOrNull()
            }

            Result.success(results)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to get major constellations: ${e.message}", e))
        }
    }
}

/**
 * Constellation with positioned stars
 */
data class ConstellationWithStars(
    val constellation: Constellation,
    val stars: List<PositionedStar>,
    val lines: List<Pair<String, String>>
)

/**
 * Star with calculated position
 */
data class PositionedStar(
    val star: Star,
    val altitudeDegrees: Double,
    val azimuthDegrees: Double,
    val isVisible: Boolean
)
