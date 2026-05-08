package com.astraloom.manager

import com.astraloom.astronomy.SunCalculator
import com.astraloom.astronomy.SunPosition
import com.astraloom.astronomy.SkyCondition
import com.astraloom.domain.Observer
import com.astraloom.usecase.ConstellationWithStars
import com.astraloom.usecase.SearchResult
import com.astraloom.usecase.VisibleStar
import kotlinx.datetime.Instant

/**
 * Swift-friendly bridge for AstraLoomManager
 *
 * Provides simple suspend functions that throw exceptions
 * instead of returning Result<T>
 */
class SwiftBridge(private val manager: AstraLoomManager) {

    private val sunCalculator = SunCalculator()

    @Throws(Exception::class)
    suspend fun getVisibleStars(
        observer: Observer,
        time: Instant,
        maxMagnitude: Double = 4.0
    ): List<VisibleStar> {
        return manager.stars.getVisibleStars(observer, time, maxMagnitude)
            .getOrThrow()
    }

    @Throws(Exception::class)
    suspend fun getConstellation(
        constellationId: String,
        observer: Observer,
        time: Instant
    ): ConstellationWithStars {
        return manager.constellations.getConstellation(constellationId, observer, time)
            .getOrThrow()
    }

    @Throws(Exception::class)
    suspend fun getVisibleConstellations(
        observer: Observer,
        time: Instant
    ): List<ConstellationWithStars> {
        return manager.constellations.getVisibleConstellations(observer, time)
            .getOrThrow()
    }

    @Throws(Exception::class)
    suspend fun searchStars(query: String): List<SearchResult> {
        return manager.stars.searchStars(query)
            .getOrThrow()
    }

    /**
     * Get sun position for given observer and time
     */
    fun getSunPosition(observer: Observer, time: Instant): SunPosition {
        return sunCalculator.calculateSunPosition(observer, time)
    }

    /**
     * Get sky condition based on sun altitude
     */
    fun getSkyCondition(sunAltitude: Double): SkyCondition {
        return sunCalculator.getSkyCondition(sunAltitude)
    }
}
