package com.astraloom.astronomy

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JulianDateTest {

    @Test
    fun testJ2000Epoch() {
        // J2000.0 = January 1, 2000, 12:00 UTC
        val jd = JulianDate.calculate(2000, 1, 1, 12.0)
        assertEquals(JulianDate.J2000, jd, 0.001, "J2000 epoch should be 2451545.0")
    }

    @Test
    fun testKnownDate() {
        // Reference: Meeus, "Astronomical Algorithms", Example 7.a
        // October 4, 1957, 19:26:24 UTC (Sputnik 1 launch)
        // Expected JD: 2436116.31
        val hour = 19 + 26 / 60.0 + 24 / 3600.0
        val jd = JulianDate.calculate(1957, 10, 4, hour)
        assertEquals(2436116.31, jd, 0.01, "Sputnik 1 launch date JD")
    }

    @Test
    fun testYear2024() {
        // January 1, 2024, 00:00 UTC
        val jd = JulianDate.calculate(2024, 1, 1, 0.0)
        // Expected: approximately 2460310.5
        assertTrue(jd > 2460310.0 && jd < 2460311.0, "JD for 2024-01-01 should be around 2460310.5")
    }

    @Test
    fun testCenturiesSinceJ2000() {
        // J2000.0 itself
        val t0 = JulianDate.centuriesSinceJ2000(JulianDate.J2000)
        assertEquals(0.0, t0, 0.0001, "T should be 0 at J2000")

        // 100 years after J2000 (36525 days)
        val jd100 = JulianDate.J2000 + 36525.0
        val t100 = JulianDate.centuriesSinceJ2000(jd100)
        assertEquals(1.0, t100, 0.0001, "T should be 1.0 after 100 years")
    }

    @Test
    fun testModifiedJulianDate() {
        val jd = 2451545.0 // J2000
        val mjd = JulianDate.toMJD(jd)
        assertEquals(51544.5, mjd, 0.001, "MJD for J2000")

        val jdBack = JulianDate.fromMJD(mjd)
        assertEquals(jd, jdBack, 0.001, "Convert back to JD")
    }

    @Test
    fun testJanuaryFebruaryAdjustment() {
        // January and February are treated as month 13 and 14 of the previous year
        // This test ensures the adjustment works correctly

        // January 15, 2024
        val jdJan = JulianDate.calculate(2024, 1, 15, 0.0)

        // March 1, 2024 (should be later)
        val jdMar = JulianDate.calculate(2024, 3, 1, 0.0)

        assertTrue(jdMar > jdJan, "March should be after January")
    }
}
