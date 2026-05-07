package com.astraloom.manager

import com.astraloom.domain.Observer
import com.astraloom.usecase.ConstellationWithStars
import com.astraloom.usecase.VisibleStar
import kotlinx.datetime.Instant

/**
 * Swift-friendly wrapper functions for AstraLoomManager
 *
 * Kotlin Result型はSwiftで扱いにくいため、
 * 成功時は値、失敗時は例外をthrowする関数を提供
 */

/**
 * Get visible stars (Swift-friendly version)
 * @throws Exception if operation fails
 */
@Throws(Exception::class)
suspend fun AstraLoomManager.getVisibleStarsOrThrow(
    observer: Observer,
    time: Instant,
    maxMagnitude: Double = 4.0
): List<VisibleStar> {
    return stars.getVisibleStars(observer, time, maxMagnitude)
        .getOrThrow()
}

/**
 * Get constellation (Swift-friendly version)
 * @throws Exception if operation fails
 */
@Throws(Exception::class)
suspend fun AstraLoomManager.getConstellationOrThrow(
    constellationId: String,
    observer: Observer,
    time: Instant
): ConstellationWithStars {
    return constellations.getConstellation(constellationId, observer, time)
        .getOrThrow()
}

/**
 * Search stars (Swift-friendly version)
 * @throws Exception if operation fails
 */
@Throws(Exception::class)
suspend fun AstraLoomManager.searchStarsOrThrow(
    query: String
): List<com.astraloom.usecase.SearchResult> {
    return stars.searchStars(query)
        .getOrThrow()
}
