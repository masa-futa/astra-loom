package com.astraloom.usecase

import com.astraloom.astronomy.AstronomyEngine
import com.astraloom.domain.Observer
import com.astraloom.domain.Star
import com.astraloom.repository.StarRepository
import kotlinx.datetime.Instant

/**
 * Use case for searching stars by name
 * (星検索ユースケース)
 */
class SearchStarsUseCase(
    private val starRepository: StarRepository,
    private val astronomyEngine: AstronomyEngine
) {
    /**
     * Search stars by name and optionally calculate their positions
     *
     * @param query Search query
     * @param observer Observer location (optional, for position calculation)
     * @param time Observation time (optional, for position calculation)
     * @return List of stars matching the query, with positions if observer/time provided
     */
    suspend fun execute(
        query: String,
        observer: Observer? = null,
        time: Instant? = null
    ): Result<List<SearchResult>> {
        return try {
            // Search stars by name
            val starsResult = starRepository.searchStarsByName(query)
            val stars = starsResult.getOrThrow()

            // If observer and time are provided, calculate positions
            val results = if (observer != null && time != null) {
                val positions = astronomyEngine.calculateStarPositions(stars, observer, time)
                stars.map { star ->
                    val position = positions[star.id]
                    SearchResult(
                        star = star,
                        altitudeDegrees = position?.altitudeDegrees(),
                        azimuthDegrees = position?.azimuthDegrees(),
                        isVisible = position?.isVisible()
                    )
                }
            } else {
                stars.map { star ->
                    SearchResult(star = star)
                }
            }

            Result.success(results)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to search stars: ${e.message}", e))
        }
    }
}

/**
 * Search result with optional position data
 */
data class SearchResult(
    val star: Star,
    val altitudeDegrees: Double? = null,
    val azimuthDegrees: Double? = null,
    val isVisible: Boolean? = null
)
