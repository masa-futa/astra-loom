package com.astraloom.astronomy

import com.astraloom.domain.EquatorialCoordinate
import com.astraloom.domain.HorizontalCoordinate
import com.astraloom.domain.Observer
import kotlin.math.*

/**
 * Coordinate transformation between equatorial and horizontal systems
 * (赤道座標系と地平座標系の変換)
 *
 * This is the core calculation for rendering stars in the sky.
 *
 * Reference:
 * - Meeus, "Astronomical Algorithms", Chapter 13
 * - astronomy-engine-implementation.md
 */
object CoordinateTransform {

    /**
     * Transform Equatorial coordinate (RA/Dec) to Horizontal coordinate (Alt/Az)
     *
     * Formulas:
     * sin(alt) = sin(dec) * sin(lat) + cos(dec) * cos(lat) * cos(HA)
     * cos(A) = (sin(dec) - sin(alt) * sin(lat)) / (cos(alt) * cos(lat))
     * Az = arccos(cos(A))
     * if sin(HA) > 0: Az = 360° - Az
     *
     * @param equatorial Equatorial coordinate (J2000)
     * @param observer Observer location
     * @param lst Local Sidereal Time in radians
     * @return Horizontal coordinate (altitude, azimuth)
     */
    fun equatorialToHorizontal(
        equatorial: EquatorialCoordinate,
        observer: Observer,
        lst: Double
    ): HorizontalCoordinate {
        val ra = equatorial.ra
        val dec = equatorial.dec
        val lat = observer.latitude

        // Calculate Hour Angle
        val ha = SiderealTime.calculateHourAngle(lst, ra)

        // Calculate altitude
        val sinAlt = sin(dec) * sin(lat) + cos(dec) * cos(lat) * cos(ha)
        val alt = asin(sinAlt)

        // Calculate azimuth
        val cosA = (sin(dec) - sin(alt) * sin(lat)) / (cos(alt) * cos(lat))

        // Clamp to [-1, 1] to avoid numerical errors in acos
        val cosAClamped = cosA.coerceIn(-1.0, 1.0)

        var az = acos(cosAClamped)

        // Adjust azimuth based on hour angle
        // If sin(HA) > 0, the object is in the western sky
        if (sin(ha) > 0) {
            az = 2 * PI - az
        }

        return HorizontalCoordinate(alt, az)
    }

    /**
     * Transform Horizontal coordinate (Alt/Az) to Equatorial coordinate (RA/Dec)
     *
     * This is the inverse transformation, useful for pointing calculations.
     *
     * Formulas:
     * sin(dec) = sin(alt) * sin(lat) + cos(alt) * cos(lat) * cos(Az)
     * cos(HA) = (sin(alt) - sin(dec) * sin(lat)) / (cos(dec) * cos(lat))
     * RA = LST - HA
     *
     * @param horizontal Horizontal coordinate
     * @param observer Observer location
     * @param lst Local Sidereal Time in radians
     * @return Equatorial coordinate
     */
    fun horizontalToEquatorial(
        horizontal: HorizontalCoordinate,
        observer: Observer,
        lst: Double
    ): EquatorialCoordinate {
        val alt = horizontal.altitude
        val az = horizontal.azimuth
        val lat = observer.latitude

        // Calculate declination
        val sinDec = sin(alt) * sin(lat) + cos(alt) * cos(lat) * cos(az)
        val dec = asin(sinDec)

        // Calculate hour angle
        val cosHA = (sin(alt) - sin(dec) * sin(lat)) / (cos(dec) * cos(lat))
        val cosHAClamped = cosHA.coerceIn(-1.0, 1.0)

        var ha = acos(cosHAClamped)

        // Adjust hour angle based on azimuth
        if (sin(az) > 0) {
            ha = 2 * PI - ha
        }

        // Calculate right ascension
        var ra = lst - ha

        // Normalize to [0, 2π]
        while (ra < 0) ra += 2 * PI
        while (ra >= 2 * PI) ra -= 2 * PI

        return EquatorialCoordinate(ra, dec)
    }

    /**
     * Calculate angular separation between two equatorial coordinates
     *
     * Uses the haversine formula for numerical stability.
     *
     * @param coord1 First coordinate
     * @param coord2 Second coordinate
     * @return Angular separation in radians
     */
    fun angularSeparation(
        coord1: EquatorialCoordinate,
        coord2: EquatorialCoordinate
    ): Double {
        val ra1 = coord1.ra
        val dec1 = coord1.dec
        val ra2 = coord2.ra
        val dec2 = coord2.dec

        // Haversine formula
        val deltaRA = ra2 - ra1
        val deltaDec = dec2 - dec1

        val a = sin(deltaDec / 2).pow(2) +
                cos(dec1) * cos(dec2) * sin(deltaRA / 2).pow(2)

        val c = 2 * asin(sqrt(a))

        return c
    }

    /**
     * Calculate parallactic angle
     *
     * The parallactic angle is the angle between the north celestial pole,
     * the object, and the zenith.
     *
     * @param equatorial Equatorial coordinate
     * @param observer Observer location
     * @param lst Local Sidereal Time
     * @return Parallactic angle in radians
     */
    fun parallacticAngle(
        equatorial: EquatorialCoordinate,
        observer: Observer,
        lst: Double
    ): Double {
        val ha = SiderealTime.calculateHourAngle(lst, equatorial.ra)
        val lat = observer.latitude
        val dec = equatorial.dec

        val numerator = sin(ha)
        val denominator = tan(lat) * cos(dec) - sin(dec) * cos(ha)

        return atan2(numerator, denominator)
    }
}
