package com.astraloom.astronomy

import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SiderealTimeTest {

    @Test
    fun testGMSTAtJ2000() {
        // At J2000.0, GMST should be approximately 280.46° (18h 41m 50s)
        val gmst = SiderealTime.calculateGMST(JulianDate.J2000)
        val gmstDegrees = Math.toDegrees(gmst)

        // Expected: around 280.46°
        assertEquals(280.46, gmstDegrees, 1.0, "GMST at J2000 should be around 280.46°")
    }

    @Test
    fun testGMSTRange() {
        // GMST should always be in [0, 2π]
        val testDates = listOf(
            JulianDate.J2000,
            JulianDate.calculate(2024, 1, 1, 0.0),
            JulianDate.calculate(2024, 6, 21, 12.0),
            JulianDate.calculate(2024, 12, 31, 23.0)
        )

        testDates.forEach { jd ->
            val gmst = SiderealTime.calculateGMST(jd)
            assertTrue(gmst >= 0.0 && gmst < 2 * PI, "GMST should be in [0, 2π], got $gmst")
        }
    }

    @Test
    fun testLSTCalculation() {
        // Tokyo longitude: 139.6503° = 2.4377 radians
        val tokyoLongitude = Math.toRadians(139.6503)
        val jd = JulianDate.calculate(2024, 1, 1, 0.0)

        val gmst = SiderealTime.calculateGMST(jd)
        val lst = SiderealTime.calculateLST(jd, tokyoLongitude)

        // LST should be GMST + longitude
        var expectedLST = gmst + tokyoLongitude
        if (expectedLST >= 2 * PI) expectedLST -= 2 * PI

        assertEquals(expectedLST, lst, 0.0001, "LST = GMST + longitude")
    }

    @Test
    fun testLSTRange() {
        // LST should always be in [0, 2π]
        val longitudes = listOf(0.0, PI / 2, PI, -PI / 2, -PI)
        val jd = JulianDate.calculate(2024, 6, 15, 12.0)

        longitudes.forEach { lon ->
            val lst = SiderealTime.calculateLST(jd, lon)
            assertTrue(lst >= 0.0 && lst < 2 * PI, "LST should be in [0, 2π], got $lst for longitude $lon")
        }
    }

    @Test
    fun testHourAngleCalculation() {
        // If an object's RA equals LST, it's on the meridian (HA = 0)
        val lst = PI / 2 // 6 hours
        val ra = PI / 2  // Same as LST

        val ha = SiderealTime.calculateHourAngle(lst, ra)
        assertEquals(0.0, ha, 0.0001, "HA should be 0 when RA = LST (object on meridian)")
    }

    @Test
    fun testHourAngleRange() {
        // Hour angle should be in [-π, π]
        val lst = Math.toRadians(180.0)
        val testRAs = listOf(0.0, PI / 4, PI / 2, PI, 3 * PI / 2)

        testRAs.forEach { ra ->
            val ha = SiderealTime.calculateHourAngle(lst, ra)
            assertTrue(ha >= -PI && ha <= PI, "HA should be in [-π, π], got $ha for RA $ra")
        }
    }

    @Test
    fun testHourAnglePositiveWest() {
        // Object west of meridian (already passed) should have positive HA
        val lst = Math.toRadians(180.0) // 12h
        val ra = Math.toRadians(90.0)   // 6h (object is 6h west)

        val ha = SiderealTime.calculateHourAngle(lst, ra)
        assertTrue(ha > 0, "HA should be positive for object west of meridian")
    }

    @Test
    fun testHourAngleNegativeEast() {
        // Object east of meridian (approaching) should have negative HA
        val lst = Math.toRadians(90.0)  // 6h
        val ra = Math.toRadians(180.0)  // 12h (object is 6h east)

        val ha = SiderealTime.calculateHourAngle(lst, ra)
        assertTrue(ha < 0, "HA should be negative for object east of meridian")
    }

    @Test
    fun testConversionBetweenRadiansAndHours() {
        val hours = 12.0
        val radians = SiderealTime.hoursToRadians(hours)
        val hoursBack = SiderealTime.radiansToHours(radians)

        assertEquals(PI, radians, 0.0001, "12 hours = π radians")
        assertEquals(hours, hoursBack, 0.0001, "Conversion roundtrip")
    }
}
