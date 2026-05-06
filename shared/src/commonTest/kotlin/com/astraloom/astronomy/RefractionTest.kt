package com.astraloom.astronomy

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RefractionTest {

    @Test
    fun testNoRefractionAtZenith() {
        // At zenith (90°), refraction should be negligible
        val zenithAlt = Math.toRadians(90.0)
        val corrected = Refraction.removeRefraction(zenithAlt)

        assertEquals(zenithAlt, corrected, 0.0001, "No refraction at zenith")
    }

    @Test
    fun testRefractionAtHorizon() {
        // At horizon (0°), refraction is approximately 34 arcminutes
        val horizonAlt = Math.toRadians(0.0)
        val corrected = Refraction.removeRefraction(horizonAlt)

        // True altitude should be lower than apparent
        assertTrue(corrected < horizonAlt, "True altitude should be lower than apparent at horizon")

        // Difference should be approximately 34 arcminutes (0.57°)
        val differenceDeg = Math.toDegrees(horizonAlt - corrected)
        assertEquals(0.57, differenceDeg, 0.1, "Refraction at horizon should be ~34 arcmin (0.57°)")
    }

    @Test
    fun testRefractionDecreaseWithAltitude() {
        // Refraction should decrease as altitude increases
        val alt10 = Math.toRadians(10.0)
        val alt30 = Math.toRadians(30.0)
        val alt60 = Math.toRadians(60.0)

        val refraction10 = Refraction.calculateRefractionAmount(alt10)
        val refraction30 = Refraction.calculateRefractionAmount(alt30)
        val refraction60 = Refraction.calculateRefractionAmount(alt60)

        assertTrue(refraction10 > refraction30, "Refraction at 10° should be > refraction at 30°")
        assertTrue(refraction30 > refraction60, "Refraction at 30° should be > refraction at 60°")
    }

    @Test
    fun testAddAndRemoveRefraction() {
        // Adding and removing refraction should return to original (approximately)
        val trueAlt = Math.toRadians(30.0)

        val apparent = Refraction.addRefraction(trueAlt)
        val backToTrue = Refraction.removeRefraction(apparent)

        assertEquals(trueAlt, backToTrue, 0.001, "Roundtrip refraction correction should return to original")
    }

    @Test
    fun testNoRefractionBelowHorizon() {
        // Below horizon, no refraction correction should be applied
        val belowHorizon = Math.toRadians(-10.0)
        val corrected = Refraction.removeRefraction(belowHorizon)

        assertEquals(belowHorizon, corrected, "No refraction below horizon")
    }

    @Test
    fun testRefractionWithAtmosphericConditions() {
        // Test refraction with different atmospheric conditions
        val apparentAlt = Math.toRadians(30.0)

        // Standard conditions
        val standard = Refraction.removeRefractionWithConditions(apparentAlt, 1010.0, 10.0)

        // High pressure
        val highPressure = Refraction.removeRefractionWithConditions(apparentAlt, 1030.0, 10.0)

        // Low pressure
        val lowPressure = Refraction.removeRefractionWithConditions(apparentAlt, 990.0, 10.0)

        // High pressure should give more refraction (true alt lower)
        assertTrue(highPressure < standard, "High pressure should increase refraction")

        // Low pressure should give less refraction (true alt higher)
        assertTrue(lowPressure > standard, "Low pressure should decrease refraction")
    }

    @Test
    fun testRefractionTemperatureEffect() {
        // Test temperature effect on refraction
        val apparentAlt = Math.toRadians(30.0)

        // Cold temperature (more refraction)
        val cold = Refraction.removeRefractionWithConditions(apparentAlt, 1010.0, -10.0)

        // Hot temperature (less refraction)
        val hot = Refraction.removeRefractionWithConditions(apparentAlt, 1010.0, 30.0)

        // Cold should give more refraction (true alt lower)
        assertTrue(cold < hot, "Cold temperature should increase refraction")
    }

    @Test
    fun testRefractionSignificance() {
        // Test which altitudes need refraction correction

        val high = Math.toRadians(60.0)
        val mid = Math.toRadians(30.0)
        val low = Math.toRadians(10.0)
        val belowHorizon = Math.toRadians(-5.0)

        assertFalse(Refraction.isRefractionSignificant(high), "Refraction not significant at 60°")
        assertTrue(Refraction.isRefractionSignificant(mid), "Refraction significant at 30°")
        assertTrue(Refraction.isRefractionSignificant(low), "Refraction significant at 10°")
        assertFalse(Refraction.isRefractionSignificant(belowHorizon), "Refraction not applied below horizon")
    }

    @Test
    fun testRefractionAmountCalculation() {
        // Test refraction amount at various altitudes
        val alt45 = Math.toRadians(45.0)
        val refraction45 = Refraction.calculateRefractionAmount(alt45)

        // At 45°, refraction should be approximately 1 arcminute (60 arcsec)
        assertEquals(60.0, refraction45, 10.0, "Refraction at 45° should be ~1 arcminute")
    }

    @Test
    fun testHorizonRefraction() {
        // Test standard horizon refraction
        val horizonRefraction = Refraction.getHorizonRefraction()

        // Should be approximately 34 arcminutes (0.0099 radians)
        val horizonRefractionArcmin = Math.toDegrees(horizonRefraction) * 60.0
        assertEquals(34.0, horizonRefractionArcmin, 1.0, "Horizon refraction should be ~34 arcminutes")
    }

    @Test
    fun testRefractionForRealStar() {
        // Simulate real observation scenario
        // Star at altitude 15° (apparent)
        val apparentAlt = Math.toRadians(15.0)

        val trueAlt = Refraction.removeRefraction(apparentAlt)

        // True altitude should be lower
        assertTrue(trueAlt < apparentAlt, "True altitude should be lower than apparent")

        // Difference should be a few arcminutes
        val diffArcmin = Math.toDegrees(apparentAlt - trueAlt) * 60.0
        assertTrue(diffArcmin > 2.0 && diffArcmin < 6.0, "Refraction at 15° should be 2-6 arcminutes")
    }

    @Test
    fun testRefractionMonotonicity() {
        // Refraction should decrease monotonically with increasing altitude
        val altitudes = listOf(5.0, 10.0, 20.0, 30.0, 45.0, 60.0, 75.0)
        var previousRefraction = Double.MAX_VALUE

        altitudes.forEach { altDeg ->
            val altRad = Math.toRadians(altDeg)
            val refraction = Refraction.calculateRefractionAmount(altRad)

            assertTrue(refraction < previousRefraction, "Refraction should decrease with altitude")
            previousRefraction = refraction
        }
    }
}
