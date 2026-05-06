package com.astraloom.manager

import com.astraloom.domain.Observer
import com.astraloom.domain.Star
import com.astraloom.usecase.ConstellationWithStars
import com.astraloom.usecase.VisibleStar
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Astra Loom Manager - Top-level facade integrating all managers
 * (全Managerを統合するトップレベルファサード)
 *
 * This is the main entry point for platform-specific code (iOS/Android).
 * It provides a unified API for all astronomy-related operations.
 *
 * Usage:
 * ```kotlin
 * val manager = AstraLoomManager.create(config)
 * val stars = manager.stars.getVisibleStars(observer, time)
 * val orion = manager.constellations.getConstellation("Ori", observer, time)
 * ```
 */
class AstraLoomManager(
    val stars: StarManager,
    val constellations: ConstellationManager,
    val astronomy: AstronomyManager
) {
    /**
     * Convenience method: Get visible stars at current time
     */
    suspend fun getVisibleStarsNow(
        observer: Observer,
        maxMagnitude: Double = 4.0
    ): Result<List<VisibleStar>> {
        return stars.getVisibleStars(observer, Clock.System.now(), maxMagnitude)
    }

    /**
     * Convenience method: Get major constellations at current time
     */
    suspend fun getMajorConstellationsNow(
        observer: Observer
    ): Result<List<ConstellationWithStars>> {
        return constellations.getMajorConstellations(observer, Clock.System.now())
    }

    /**
     * Convenience method: Search stars with positions at current time
     */
    suspend fun searchStarsNow(
        query: String,
        observer: Observer
    ): Result<List<com.astraloom.usecase.SearchResult>> {
        return stars.searchStars(query, observer, Clock.System.now())
    }

    /**
     * Convenience method: Calculate star position at current time
     */
    suspend fun calculateStarPositionNow(
        star: Star,
        observer: Observer
    ): Result<com.astraloom.astronomy.AstronomyEngine.StarPosition> {
        return astronomy.calculateStarPosition(star, observer)
    }

    /**
     * Get night sky summary
     *
     * @param observer Observer location
     * @param time Observation time
     * @return Summary of visible stars and constellations
     */
    suspend fun getNightSkySummary(
        observer: Observer,
        time: Instant = Clock.System.now()
    ): Result<NightSkySummary> {
        return try {
            val visibleStarsResult = stars.getVisibleStars(observer, time)
            val visibleConstellationsResult = constellations.getVisibleConstellations(observer, time)

            if (visibleStarsResult.isFailure || visibleConstellationsResult.isFailure) {
                return Result.failure(
                    Exception("Failed to get night sky summary")
                )
            }

            val visibleStars = visibleStarsResult.getOrThrow()
            val visibleConstellations = visibleConstellationsResult.getOrThrow()

            Result.success(
                NightSkySummary(
                    visibleStarsCount = visibleStars.size,
                    visibleConstellationsCount = visibleConstellations.size,
                    brightestStar = visibleStars.firstOrNull(),
                    highestStar = visibleStars.maxByOrNull { it.altitudeDegrees },
                    observer = observer,
                    time = time
                )
            )
        } catch (e: Exception) {
            Result.failure(Exception("Failed to get night sky summary: ${e.message}", e))
        }
    }

    companion object {
        /**
         * Create AstraLoomManager with default configuration
         * This will be used by platform-specific code
         */
        fun create(config: AstraLoomConfig): AstraLoomManager {
            // This will be implemented with ManagerFactory
            throw NotImplementedError("Use ManagerFactory.create() instead")
        }
    }
}

/**
 * Night sky summary
 */
data class NightSkySummary(
    val visibleStarsCount: Int,
    val visibleConstellationsCount: Int,
    val brightestStar: VisibleStar?,
    val highestStar: VisibleStar?,
    val observer: Observer,
    val time: Instant
)

/**
 * Configuration for AstraLoomManager
 */
data class AstraLoomConfig(
    val cacheStrategy: com.astraloom.data.cache.CacheStrategy = com.astraloom.data.cache.CacheStrategy.CACHE_FIRST,
    val cacheExpirationMs: Long = 3600_000L,
    val applyPrecession: Boolean = true,
    val applyRefraction: Boolean = true,
    val apiBaseUrl: String? = null
)
