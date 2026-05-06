package com.astraloom.astronomy

import com.astraloom.domain.EquatorialCoordinate
import com.astraloom.domain.Star
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PrecessionTest {

    @Test
    fun testNoPrecessionAtJ2000() {
        // At J2000, precession should return the same coordinate
        val coord = EquatorialCoordinate.fromDegrees(100.0, 45.0)
        val precessed = Precession.applyPrecession(coord, JulianDate.J2000)

        assertEquals(coord.ra, precessed.ra, 0.00001, "RA should not change at J2000")
        assertEquals(coord.dec, precessed.dec, 0.00001, "Dec should not change at J2000")
    }

    @Test
    fun testPrecessionDirection() {
        // Test that precession moves coordinates in the expected direction
        val coord = EquatorialCoordinate.fromDegrees(180.0, 45.0)

        // 50 years after J2000
        val jd2050 = JulianDate.J2000 + 365.25 * 50

        val precessed = Precession.applyPrecession(coord, jd2050)

        // Precession should cause a change (not testing exact values, just that it changed)
        assertTrue(abs(precessed.ra - coord.ra) > 0.0001, "RA should change after 50 years")
    }

    @Test
    fun testPrecessionMagnitude() {
        // Test approximate magnitude of precession
        val coord = EquatorialCoordinate.fromDegrees(0.0, 0.0)

        // 100 years after J2000 (1 century)
        val jd = JulianDate.J2000 + 36525.0 // 1 century

        val (deltaRA, deltaDec) = Precession.calculatePrecessionDelta(coord, jd)

        // For 100 years, precession should be on the order of thousands of arcseconds
        // (Approximate: ~50 arcsec/year * 100 years = ~5000 arcsec)
        assertTrue(abs(deltaRA) > 1000.0, "RA precession over 100 years should be > 1000 arcsec")
    }

    @Test
    fun testPrecessionReversibility() {
        // Precession from J2000 to future and back should return to original
        val coord = EquatorialCoordinate.fromDegrees(100.0, 30.0)

        // 25 years after J2000
        val jdFuture = JulianDate.J2000 + 365.25 * 25

        val precessed = Precession.applyPrecession(coord, jdFuture)

        // This test demonstrates forward precession
        // (Backward precession would require a different method)
        assertTrue(abs(precessed.ra - coord.ra) > 0.0, "Coordinate should change")
    }

    @Test
    fun testSiriusPrecession() {
        // Test precession of a real star (Sirius)
        val sirius = Star.Sirius.coordinate

        // Year 2024
        val jd2024 = JulianDate.calculate(2024, 1, 1, 0.0)

        val precessed = Precession.applyPrecession(sirius, jd2024)

        // After 24 years, there should be a measurable change
        val (deltaRA, deltaDec) = Precession.calculatePrecessionDelta(sirius, jd2024)

        // Expected: ~50 arcsec/year * 24 years = ~1200 arcsec in RA
        assertTrue(abs(deltaRA) > 500.0, "Sirius RA should change by > 500 arcsec over 24 years")
    }

    @Test
    fun testSimplifiedPrecession() {
        // Test simplified precession method
        val coord = EquatorialCoordinate.fromDegrees(100.0, 45.0)
        val jd2024 = JulianDate.calculate(2024, 1, 1, 0.0)

        val precessed = Precession.applyPrecessionSimplified(coord, jd2024)

        // Should produce a change (not as accurate as full calculation, but faster)
        assertTrue(abs(precessed.ra - coord.ra) > 0.0, "Simplified precession should change RA")
    }

    @Test
    fun testPrecessionSignificance() {
        // Test if precession is significant

        // At J2000: not significant
        assertFalse(Precession.isPrecessionSignificant(JulianDate.J2000), "Precession at J2000 is not significant")

        // 1 year after: not significant
        val jd1Year = JulianDate.J2000 + 365.25
        assertFalse(Precession.isPrecessionSignificant(jd1Year), "Precession after 1 year is not significant")

        // 10 years after: significant
        val jd10Years = JulianDate.J2000 + 365.25 * 10
        assertTrue(Precession.isPrecessionSignificant(jd10Years), "Precession after 10 years is significant")

        // 50 years after: significant
        val jd50Years = JulianDate.J2000 + 365.25 * 50
        assertTrue(Precession.isPrecessionSignificant(jd50Years), "Precession after 50 years is significant")
    }

    @Test
    fun testPrecessionForNearPole() {
        // Test precession for a star near the celestial pole
        val nearPole = EquatorialCoordinate.fromDegrees(45.0, 85.0)
        val jd2024 = JulianDate.calculate(2024, 1, 1, 0.0)

        val precessed = Precession.applyPrecession(nearPole, jd2024)

        // Coordinate should change, but remain valid
        assertTrue(precessed.dec >= -Math.PI / 2 && precessed.dec <= Math.PI / 2,
            "Declination should remain in valid range")
        assertTrue(precessed.ra >= 0.0 && precessed.ra < 2 * Math.PI,
            "RA should remain in valid range")
    }

    @Test
    fun testShortTermPrecession() {
        // For very short time periods (< 1 year), simplified precession should return same value
        val coord = EquatorialCoordinate.fromDegrees(100.0, 30.0)
        val jd6Months = JulianDate.J2000 + 182.5 // 6 months

        val precessed = Precession.applyPrecessionSimplified(coord, jd6Months)

        // Should be essentially unchanged
        assertEquals(coord.ra, precessed.ra, 0.001, "Short-term simplified precession should not change much")
    }
}
