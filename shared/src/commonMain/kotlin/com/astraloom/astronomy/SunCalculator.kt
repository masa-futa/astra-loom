package com.astraloom.astronomy

import com.astraloom.domain.Observer
import com.astraloom.util.toDegrees
import com.astraloom.util.toRadians
import kotlinx.datetime.Instant
import kotlin.math.*

/**
 * Sun position calculator
 * (太陽位置計算)
 */
class SunCalculator {
    /**
     * Calculate sun's altitude and azimuth at given time and location
     *
     * @param observer Observer location
     * @param time Observation time
     * @return SunPosition with altitude and azimuth in degrees
     */
    fun calculateSunPosition(observer: Observer, time: Instant): SunPosition {
        val jd = time.epochSeconds / 86400.0 + 2440587.5
        val T = (jd - 2451545.0) / 36525.0

        // Sun's mean longitude (degrees)
        val L0 = (280.46646 + 36000.76983 * T + 0.0003032 * T * T) % 360.0

        // Sun's mean anomaly (degrees)
        val M = (357.52911 + 35999.05029 * T - 0.0001537 * T * T) % 360.0
        val MRad = M.toRadians()

        // Sun's equation of center
        val C = (1.914602 - 0.004817 * T - 0.000014 * T * T) * sin(MRad) +
                (0.019993 - 0.000101 * T) * sin(2 * MRad) +
                0.000289 * sin(3 * MRad)

        // Sun's true longitude
        val sunLon = L0 + C

        // Sun's apparent longitude
        val omega = 125.04 - 1934.136 * T
        val lambda = sunLon - 0.00569 - 0.00478 * sin(omega.toRadians())

        // Obliquity of ecliptic
        val epsilon = 23.439291 - 0.0130042 * T - 0.00000164 * T * T + 0.000000504 * T * T * T
        val epsilonRad = epsilon.toRadians()

        // Sun's right ascension and declination
        val lambdaRad = lambda.toRadians()
        var ra = atan2(cos(epsilonRad) * sin(lambdaRad), cos(lambdaRad)).toDegrees()
        if (ra < 0) ra += 360.0  // Normalize to 0-360
        val dec = asin(sin(epsilonRad) * sin(lambdaRad)).toDegrees()

        // Convert observer coordinates to degrees
        val latDeg = observer.latitude.toDegrees()
        val lonDeg = observer.longitude.toDegrees()

        // Local sidereal time
        val lst = calculateLocalSiderealTime(jd, lonDeg)

        // Hour angle (LST - RA, normalized to -180 to 180)
        var ha = lst - ra
        if (ha < -180) ha += 360.0
        if (ha > 180) ha -= 360.0

        // Convert to horizontal coordinates (use degrees, then convert to radians)
        val latRad = latDeg.toRadians()
        val decRad = dec.toRadians()
        val haRad = ha.toRadians()

        // Altitude
        val sinAlt = sin(latRad) * sin(decRad) + cos(latRad) * cos(decRad) * cos(haRad)
        val altitude = asin(sinAlt).toDegrees()

        // Azimuth (from North = 0°, East = 90°, South = 180°, West = 270°)
        val y = sin(haRad)
        val x = cos(haRad) * sin(latRad) - tan(decRad) * cos(latRad)
        var azimuth = atan2(y, x).toDegrees()
        azimuth = (azimuth + 180.0) % 360.0  // Convert from -180~180 to 0~360, and adjust reference

        return SunPosition(
            altitudeDegrees = altitude,
            azimuthDegrees = azimuth
        )
    }

    /**
     * Calculate local sidereal time
     */
    private fun calculateLocalSiderealTime(jd: Double, longitude: Double): Double {
        val T = (jd - 2451545.0) / 36525.0
        var theta = 280.46061837 + 360.98564736629 * (jd - 2451545.0) +
                0.000387933 * T * T - T * T * T / 38710000.0
        theta = (theta % 360.0 + 360.0) % 360.0
        return (theta + longitude + 360.0) % 360.0
    }

    /**
     * Get sky condition based on sun altitude
     */
    fun getSkyCondition(sunAltitude: Double): SkyCondition {
        return when {
            sunAltitude > 0 -> SkyCondition.DAY
            sunAltitude > -6 -> SkyCondition.CIVIL_TWILIGHT
            sunAltitude > -12 -> SkyCondition.NAUTICAL_TWILIGHT
            sunAltitude > -18 -> SkyCondition.ASTRONOMICAL_TWILIGHT
            else -> SkyCondition.NIGHT
        }
    }
}

/**
 * Sun position data
 */
data class SunPosition(
    val altitudeDegrees: Double,
    val azimuthDegrees: Double
) {
    /** Check if sun is above horizon */
    fun isDaytime(): Boolean = altitudeDegrees > 0

    /** Check if it's dark enough for star observation */
    fun isAstronomicalNight(): Boolean = altitudeDegrees < -18.0

    /** Check if it's good for star observation (astronomical twilight or darker) */
    fun isGoodForStargazing(): Boolean = altitudeDegrees < -12.0
}

/**
 * Sky condition based on sun altitude
 */
enum class SkyCondition {
    DAY,                    // Sun above horizon
    CIVIL_TWILIGHT,         // Sun 0° to -6° (bright twilight)
    NAUTICAL_TWILIGHT,      // Sun -6° to -12° (medium twilight)
    ASTRONOMICAL_TWILIGHT,  // Sun -12° to -18° (dark twilight)
    NIGHT                   // Sun below -18° (true night)
}
