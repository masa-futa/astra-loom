package com.astraloom.astronomy
import com.astraloom.util.toRadians
import com.astraloom.util.toDegrees

import kotlin.math.PI

/**
 * Sidereal Time calculations (恒星時計算)
 *
 * Sidereal time is a measure of Earth's rotation relative to the stars.
 * It's essential for converting between equatorial and horizontal coordinates.
 *
 * Reference: Meeus, "Astronomical Algorithms", Chapter 12
 */
object SiderealTime {

    /**
     * Calculate Greenwich Mean Sidereal Time (GMST) in radians
     *
     * Formula (from astronomy-engine-design.md):
     * GMST = 280.46061837
     *      + 360.98564736629 * (JD - 2451545.0)
     *      + 0.000387933 * T²
     *      - (T³ / 38710000)
     *
     * Where T = centuries since J2000.0
     *
     * @param jd Julian Date
     * @return GMST in radians [0, 2π]
     */
    fun calculateGMST(jd: Double): Double {
        val T = JulianDate.centuriesSinceJ2000(jd)

        // Calculate GMST in degrees
        var gmstDegrees = 280.46061837 +
                360.98564736629 * (jd - JulianDate.J2000) +
                0.000387933 * T * T -
                (T * T * T) / 38710000.0

        // Normalize to [0, 360) degrees
        gmstDegrees = normalizeDegrees(gmstDegrees)

        // Convert to radians
        return (gmstDegrees).toRadians()
    }

    /**
     * Calculate Local Sidereal Time (LST) in radians
     *
     * Formula:
     * LST = GMST + longitude
     *
     * @param jd Julian Date
     * @param longitudeRadians Observer's longitude in radians (East positive)
     * @return LST in radians [0, 2π]
     */
    fun calculateLST(jd: Double, longitudeRadians: Double): Double {
        val gmst = calculateGMST(jd)

        // Add longitude (already in radians)
        var lst = gmst + longitudeRadians

        // Normalize to [0, 2π]
        lst = normalizeRadians(lst)

        return lst
    }

    /**
     * Calculate Hour Angle (HA) in radians
     *
     * Formula:
     * HA = LST - RA
     *
     * Hour angle is the angle between the meridian and the celestial object.
     * Positive = object is west of meridian (already passed)
     * Negative = object is east of meridian (approaching)
     *
     * @param lst Local Sidereal Time in radians
     * @param raRadians Right Ascension in radians
     * @return Hour Angle in radians [-π, π]
     */
    fun calculateHourAngle(lst: Double, raRadians: Double): Double {
        var ha = lst - raRadians

        // Normalize to [-π, π]
        while (ha > PI) ha -= 2 * PI
        while (ha < -PI) ha += 2 * PI

        return ha
    }

    /**
     * Normalize degrees to [0, 360)
     */
    private fun normalizeDegrees(degrees: Double): Double {
        var normalized = degrees % 360.0
        if (normalized < 0) normalized += 360.0
        return normalized
    }

    /**
     * Normalize radians to [0, 2π]
     */
    private fun normalizeRadians(radians: Double): Double {
        var normalized = radians % (2 * PI)
        if (normalized < 0) normalized += 2 * PI
        return normalized
    }

    /**
     * Convert sidereal time from radians to hours
     */
    fun radiansToHours(radians: Double): Double {
        return radians * 12.0 / PI // 2π radians = 24 hours
    }

    /**
     * Convert sidereal time from hours to radians
     */
    fun hoursToRadians(hours: Double): Double {
        return hours * PI / 12.0 // 24 hours = 2π radians
    }
}
