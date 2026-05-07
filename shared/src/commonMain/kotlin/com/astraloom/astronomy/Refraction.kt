package com.astraloom.astronomy

import com.astraloom.util.toRadians
import com.astraloom.util.toDegrees
import kotlin.math.*

/**
 * Atmospheric refraction calculations (大気差補正)
 *
 * Atmospheric refraction causes celestial objects to appear higher in the sky
 * than their true geometric position. The effect is strongest near the horizon.
 *
 * Reference:
 * - Meeus, "Astronomical Algorithms", Chapter 16
 * - astronomy-engine-implementation.md
 */
object Refraction {

    /**
     * Apply atmospheric refraction correction to altitude
     *
     * Formula (simplified Bennett's formula):
     * R = 1.02 / tan(h + 10.3/(h+5.11))
     *
     * Where:
     * - R is refraction in arcminutes
     * - h is apparent altitude in degrees
     *
     * This formula is accurate to ±0.07' for h > 15°
     *
     * @param apparentAltitudeRad Apparent altitude in radians (what you see)
     * @return True (geometric) altitude in radians
     */
    fun removeRefraction(apparentAltitudeRad: Double): Double {
        // Don't apply refraction below horizon or for negative altitudes
        if (apparentAltitudeRad < 0.0) {
            return apparentAltitudeRad
        }

        val apparentAltDeg = apparentAltitudeRad.toDegrees()

        // For very high altitudes (> 85°), refraction is negligible
        if (apparentAltDeg > 85.0) {
            return apparentAltitudeRad
        }

        // Calculate refraction in arcminutes using Bennett's formula
        val refractionArcmin = if (apparentAltDeg > 0.0) {
            1.02 / tan((apparentAltDeg + 10.3 / (apparentAltDeg + 5.11)).toRadians())
        } else {
            // Near horizon, use approximation
            34.0 // Approximately 34 arcminutes at horizon
        }

        // Convert arcminutes to radians
        val refractionRad = (refractionArcmin / 60.0).toRadians()

        // True altitude = Apparent altitude - Refraction
        return apparentAltitudeRad - refractionRad
    }

    /**
     * Apply atmospheric refraction correction (inverse operation)
     *
     * Converts true geometric altitude to apparent altitude
     *
     * @param trueAltitudeRad True (geometric) altitude in radians
     * @return Apparent altitude in radians (what you would see)
     */
    fun addRefraction(trueAltitudeRad: Double): Double {
        // Don't apply refraction below horizon
        if (trueAltitudeRad < 0.0) {
            return trueAltitudeRad
        }

        val trueAltDeg = (trueAltitudeRad).toDegrees()

        // For very high altitudes, refraction is negligible
        if (trueAltDeg > 85.0) {
            return trueAltitudeRad
        }

        // Calculate refraction (approximate)
        val refractionArcmin = if (trueAltDeg > 0.0) {
            1.02 / tan((trueAltDeg + 10.3 / (trueAltDeg + 5.11).toRadians()))
        } else {
            34.0
        }

        val refractionRad = (refractionArcmin / 60.0).toRadians()

        // Apparent altitude = True altitude + Refraction
        return trueAltitudeRad + refractionRad
    }

    /**
     * Calculate refraction correction with atmospheric conditions
     *
     * More accurate formula that accounts for temperature and pressure.
     *
     * Formula:
     * R = R₀ × (P / 1010) × (283 / (273 + T))
     *
     * Where:
     * - R₀ is standard refraction
     * - P is pressure in millibars
     * - T is temperature in Celsius
     *
     * @param apparentAltitudeRad Apparent altitude in radians
     * @param pressureMillibars Atmospheric pressure (default: 1010 mb)
     * @param temperatureCelsius Temperature (default: 10°C)
     * @return True altitude in radians
     */
    fun removeRefractionWithConditions(
        apparentAltitudeRad: Double,
        pressureMillibars: Double = 1010.0,
        temperatureCelsius: Double = 10.0
    ): Double {
        if (apparentAltitudeRad < 0.0) {
            return apparentAltitudeRad
        }

        val apparentAltDeg = (apparentAltitudeRad).toDegrees()

        if (apparentAltDeg > 85.0) {
            return apparentAltitudeRad
        }

        // Standard refraction
        val refractionStandard = if (apparentAltDeg > 0.0) {
            1.02 / tan((apparentAltDeg + 10.3 / (apparentAltDeg + 5.11).toRadians()))
        } else {
            34.0
        }

        // Apply atmospheric correction factors
        val pressureFactor = pressureMillibars / 1010.0
        val temperatureFactor = 283.0 / (273.0 + temperatureCelsius)

        val refractionArcmin = refractionStandard * pressureFactor * temperatureFactor
        val refractionRad = (refractionArcmin / 60.0).toRadians()

        return apparentAltitudeRad - refractionRad
    }

    /**
     * Calculate refraction correction amount in arcseconds
     *
     * @param altitudeRad Altitude in radians
     * @return Refraction correction in arcseconds
     */
    fun calculateRefractionAmount(altitudeRad: Double): Double {
        if (altitudeRad < 0.0 || altitudeRad > (85.0).toRadians()) {
            return 0.0
        }

        val altDeg = (altitudeRad).toDegrees()
        val refractionArcmin = 1.02 / tan((altDeg + 10.3 / (altDeg + 5.11).toRadians()))

        return refractionArcmin * 60.0 // Convert to arcseconds
    }

    /**
     * Check if refraction correction is significant
     *
     * For altitudes > 45°, refraction is typically < 1 arcminute
     * and may be negligible for some applications.
     *
     * @param altitudeRad Altitude in radians
     * @return true if refraction correction should be applied
     */
    fun isRefractionSignificant(altitudeRad: Double): Boolean {
        // Below 45°, refraction > 1 arcminute
        return altitudeRad < (45.0).toRadians() && altitudeRad > 0.0
    }

    /**
     * Get refraction at horizon (approximately 34 arcminutes)
     */
    fun getHorizonRefraction(): Double {
        return (34.0 / 60.0).toRadians() // ~34 arcminutes in radians
    }
}
