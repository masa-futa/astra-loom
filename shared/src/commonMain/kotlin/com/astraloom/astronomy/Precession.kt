package com.astraloom.astronomy

import com.astraloom.domain.EquatorialCoordinate
import com.astraloom.util.toRadians
import com.astraloom.util.toDegrees
import kotlin.math.*

/**
 * Precession calculations (歳差補正)
 *
 * Precession is the slow movement of Earth's rotation axis,
 * which causes the coordinates of stars to change over time.
 *
 * This implementation uses IAU 1976 precession model (simplified).
 *
 * Reference:
 * - Meeus, "Astronomical Algorithms", Chapter 21
 * - astronomy-engine-design.md
 */
object Precession {

    /**
     * Apply precession correction from J2000.0 to a given epoch
     *
     * Formulas (IAU 1976):
     * ζ = (2306.2181*T + 0.30188*T² + 0.017998*T³) / 3600
     * z = (2306.2181*T + 1.09468*T² + 0.018203*T³) / 3600
     * θ = (2004.3109*T - 0.42665*T² - 0.041833*T³) / 3600
     *
     * Where T = centuries since J2000.0
     *
     * @param j2000Coordinate Coordinate at J2000.0 epoch
     * @param jd Julian Date of target epoch
     * @return Precessed coordinate at target epoch
     */
    fun applyPrecession(
        j2000Coordinate: EquatorialCoordinate,
        jd: Double
    ): EquatorialCoordinate {
        // If we're at J2000, no correction needed
        if (abs(jd - JulianDate.J2000) < 0.01) {
            return j2000Coordinate
        }

        val T = JulianDate.centuriesSinceJ2000(jd)

        // Calculate precession angles in arcseconds, then convert to degrees
        val zetaDeg = (2306.2181 * T + 0.30188 * T * T + 0.017998 * T * T * T) / 3600.0
        val zDeg = (2306.2181 * T + 1.09468 * T * T + 0.018203 * T * T * T) / 3600.0
        val thetaDeg = (2004.3109 * T - 0.42665 * T * T - 0.041833 * T * T * T) / 3600.0

        // Convert to radians
        val zeta = zetaDeg.toRadians()
        val z = zDeg.toRadians()
        val theta = thetaDeg.toRadians()

        // Original coordinates
        val ra0 = j2000Coordinate.ra
        val dec0 = j2000Coordinate.dec

        // Apply rotation matrix (simplified calculation)
        val A = cos(dec0) * sin(ra0 + zeta)
        val B = cos(theta) * cos(dec0) * cos(ra0 + zeta) - sin(theta) * sin(dec0)
        val C = sin(theta) * cos(dec0) * cos(ra0 + zeta) + cos(theta) * sin(dec0)

        // Calculate new RA and Dec
        var ra = atan2(A, B) + z
        val dec = asin(C)

        // Normalize RA to [0, 2π]
        while (ra < 0) ra += 2 * PI
        while (ra >= 2 * PI) ra -= 2 * PI

        return EquatorialCoordinate(ra, dec)
    }

    /**
     * Apply precession correction (simplified version for moderate accuracy)
     *
     * This is a faster approximation suitable for most visualization purposes.
     * Accuracy: ~1 arcsecond for |T| < 1 century
     *
     * @param j2000Coordinate Coordinate at J2000.0 epoch
     * @param jd Julian Date of target epoch
     * @return Precessed coordinate at target epoch
     */
    fun applyPrecessionSimplified(
        j2000Coordinate: EquatorialCoordinate,
        jd: Double
    ): EquatorialCoordinate {
        val T = JulianDate.centuriesSinceJ2000(jd)

        // Small correction for short time periods
        if (abs(T) < 0.01) { // Less than 1 year
            return j2000Coordinate
        }

        // Mean precession rates (approximate)
        // RA: ~50.3 arcsec/year in RA = 3.07 seconds/year
        // Dec: ~20.0 arcsec/year (depends on declination)

        val ra0 = j2000Coordinate.ra
        val dec0 = j2000Coordinate.dec

        // Annual precession in RA (approximate)
        val deltaRAPerYear = (50.3 / 3600.0) / cos(dec0)
        val deltaRA = deltaRAPerYear * T * 100.0 // T is in centuries

        // Annual precession in Dec (approximate, simplified)
        val deltaDecPerYear = (20.0 / 3600.0) * sin(ra0)
        val deltaDec = deltaDecPerYear * T * 100.0

        var ra = ra0 + deltaRA
        val dec = dec0 + deltaDec

        // Normalize RA
        while (ra < 0) ra += 2 * PI
        while (ra >= 2 * PI) ra -= 2 * PI

        return EquatorialCoordinate(ra, dec)
    }

    /**
     * Calculate the change in coordinates due to precession
     *
     * @param j2000Coordinate Original coordinate
     * @param jd Target Julian Date
     * @return Pair of (ΔRA in arcseconds, ΔDec in arcseconds)
     */
    fun calculatePrecessionDelta(
        j2000Coordinate: EquatorialCoordinate,
        jd: Double
    ): Pair<Double, Double> {
        val precessed = applyPrecession(j2000Coordinate, jd)

        val deltaRA = (precessed.ra - j2000Coordinate.ra) * cos(j2000Coordinate.dec)
        val deltaDec = precessed.dec - j2000Coordinate.dec

        // Convert to arcseconds
        val deltaRAArcsec = deltaRA.toDegrees() * 3600.0
        val deltaDecArcsec = deltaDec.toDegrees() * 3600.0

        return Pair(deltaRAArcsec, deltaDecArcsec)
    }

    /**
     * Check if precession correction is significant
     *
     * For most naked-eye observations, precession < 1 arcminute is negligible.
     *
     * @param jd Julian Date
     * @return true if precession correction should be applied
     */
    fun isPrecessionSignificant(jd: Double): Boolean {
        val T = abs(JulianDate.centuriesSinceJ2000(jd))
        // For |T| > 0.02 (2 years), precession > 1 arcsecond
        return T > 0.02
    }
}
