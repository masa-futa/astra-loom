package com.astraloom.astronomy

import com.astraloom.domain.EquatorialCoordinate
import com.astraloom.domain.HorizontalCoordinate
import com.astraloom.domain.Observer
import com.astraloom.domain.Star
import kotlinx.datetime.Instant

/**
 * Main astronomy engine that integrates all calculations
 * (天文計算エンジンの統合インターフェース)
 *
 * This is the high-level API for calculating star positions.
 * It combines all lower-level calculations into a simple interface.
 */
class AstronomyEngine(
    private val config: EngineConfig = EngineConfig()
) {

    /**
     * Configuration for the astronomy engine
     *
     * @property applyPrecession Whether to apply precession correction (default: true)
     * @property applyRefraction Whether to apply atmospheric refraction (default: true)
     * @property pressureMillibars Atmospheric pressure for refraction (default: 1010 mb)
     * @property temperatureCelsius Temperature for refraction (default: 10°C)
     */
    data class EngineConfig(
        val applyPrecession: Boolean = true,
        val applyRefraction: Boolean = true,
        val pressureMillibars: Double = 1010.0,
        val temperatureCelsius: Double = 10.0
    )

    /**
     * Result of star position calculation
     *
     * @property horizontal Horizontal coordinate (Alt/Az) - what you see in the sky
     * @property jd Julian Date of calculation
     * @property lst Local Sidereal Time
     * @property hourAngle Hour angle
     */
    data class StarPosition(
        val horizontal: HorizontalCoordinate,
        val jd: Double,
        val lst: Double,
        val hourAngle: Double
    ) {
        /** Check if the star is visible (above horizon) */
        fun isVisible(): Boolean = horizontal.isVisible()

        /** Get altitude in degrees */
        fun altitudeDegrees(): Double = horizontal.altitudeToDegrees()

        /** Get azimuth in degrees */
        fun azimuthDegrees(): Double = horizontal.azimuthToDegrees()
    }

    /**
     * Calculate the position of a star in the sky
     *
     * This is the main method that combines all calculations:
     * 1. Apply precession (if enabled)
     * 2. Calculate Local Sidereal Time
     * 3. Transform to horizontal coordinates
     * 4. Apply atmospheric refraction (if enabled)
     *
     * @param star Star to calculate position for
     * @param observer Observer location
     * @param time Observation time (UTC)
     * @return Star position in the sky
     */
    fun calculateStarPosition(
        star: Star,
        observer: Observer,
        time: Instant
    ): StarPosition {
        // Convert time to Julian Date
        val jd = JulianDate.fromInstant(time)

        return calculateStarPosition(star, observer, jd)
    }

    /**
     * Calculate star position using Julian Date
     *
     * @param star Star to calculate position for
     * @param observer Observer location
     * @param jd Julian Date
     * @return Star position in the sky
     */
    fun calculateStarPosition(
        star: Star,
        observer: Observer,
        jd: Double
    ): StarPosition {
        // Step 1: Apply precession correction if enabled
        val coordinate = if (config.applyPrecession && Precession.isPrecessionSignificant(jd)) {
            Precession.applyPrecession(star.coordinate, jd)
        } else {
            star.coordinate
        }

        // Step 2: Calculate Local Sidereal Time
        val lst = SiderealTime.calculateLST(jd, observer.longitude)

        // Step 3: Calculate hour angle
        val ha = SiderealTime.calculateHourAngle(lst, coordinate.ra)

        // Step 4: Transform to horizontal coordinates (geometric position)
        val geometricHorizontal = CoordinateTransform.equatorialToHorizontal(
            coordinate,
            observer,
            lst
        )

        // Step 5: Apply atmospheric refraction if enabled
        val finalHorizontal = if (config.applyRefraction && geometricHorizontal.altitude > 0.0) {
            val apparentAlt = Refraction.addRefraction(geometricHorizontal.altitude)
            HorizontalCoordinate(apparentAlt, geometricHorizontal.azimuth)
        } else {
            geometricHorizontal
        }

        return StarPosition(finalHorizontal, jd, lst, ha)
    }

    /**
     * Calculate positions for multiple stars
     *
     * @param stars List of stars
     * @param observer Observer location
     * @param time Observation time
     * @return Map of star ID to position
     */
    fun calculateStarPositions(
        stars: List<Star>,
        observer: Observer,
        time: Instant
    ): Map<String, StarPosition> {
        val jd = JulianDate.fromInstant(time)
        return stars.associate { star ->
            star.id to calculateStarPosition(star, observer, jd)
        }
    }

    /**
     * Calculate only visible stars (above horizon)
     *
     * @param stars List of stars
     * @param observer Observer location
     * @param time Observation time
     * @return Map of star ID to position (only visible stars)
     */
    fun calculateVisibleStars(
        stars: List<Star>,
        observer: Observer,
        time: Instant
    ): Map<String, StarPosition> {
        return calculateStarPositions(stars, observer, time)
            .filter { (_, position) -> position.isVisible() }
    }

    /**
     * Calculate when a star will be at a specific altitude
     *
     * This is useful for rise/set calculations
     *
     * @param star Star
     * @param observer Observer location
     * @param targetAltitudeRad Target altitude in radians (0 for horizon)
     * @param jdStart Starting Julian Date for search
     * @return Approximate Julian Date when star reaches target altitude (or null if not found)
     */
    fun findStarAtAltitude(
        star: Star,
        observer: Observer,
        targetAltitudeRad: Double,
        jdStart: Double,
        searchDays: Double = 1.0
    ): Double? {
        val steps = 100
        val stepSize = searchDays / steps

        for (i in 0 until steps) {
            val jd = jdStart + i * stepSize
            val position = calculateStarPosition(star, observer, jd)

            if (kotlin.math.abs(position.horizontal.altitude - targetAltitudeRad) < 0.01) {
                return jd
            }
        }

        return null
    }

    /**
     * Get stars sorted by altitude (highest first)
     *
     * @param stars List of stars
     * @param observer Observer location
     * @param time Observation time
     * @return List of pairs (Star, Position) sorted by altitude descending
     */
    fun getStarsSortedByAltitude(
        stars: List<Star>,
        observer: Observer,
        time: Instant
    ): List<Pair<Star, StarPosition>> {
        val positions = calculateStarPositions(stars, observer, time)
        return stars.mapNotNull { star ->
            positions[star.id]?.let { star to it }
        }.sortedByDescending { (_, position) -> position.horizontal.altitude }
    }

    /**
     * Calculate angular distance between two stars as seen from Earth
     *
     * @param star1 First star
     * @param star2 Second star
     * @param jd Julian Date (for precession)
     * @return Angular separation in radians
     */
    fun calculateAngularSeparation(
        star1: Star,
        star2: Star,
        jd: Double = JulianDate.J2000
    ): Double {
        val coord1 = if (config.applyPrecession) {
            Precession.applyPrecession(star1.coordinate, jd)
        } else {
            star1.coordinate
        }

        val coord2 = if (config.applyPrecession) {
            Precession.applyPrecession(star2.coordinate, jd)
        } else {
            star2.coordinate
        }

        return CoordinateTransform.angularSeparation(coord1, coord2)
    }
}
