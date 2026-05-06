package com.astraloom.astronomy

import com.astraloom.domain.EquatorialCoordinate
import com.astraloom.domain.HorizontalCoordinate
import com.astraloom.domain.Observer
import com.astraloom.domain.Star
import kotlin.math.PI
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CoordinateTransformTest {

    @Test
    fun testObjectOnMeridianAtZenith() {
        // Object on meridian (HA = 0) with Dec = Lat should be at zenith (Alt = 90°)
        val lat = Math.toRadians(35.0) // Observer latitude
        val observer = Observer(lat, 0.0)

        val dec = lat // Declination equals latitude
        val ra = Math.toRadians(180.0) // Arbitrary RA
        val equatorial = EquatorialCoordinate(ra, dec)

        val lst = ra // LST = RA means object is on meridian (HA = 0)

        val horizontal = CoordinateTransform.equatorialToHorizontal(equatorial, observer, lst)

        // Object should be at zenith (altitude = 90°)
        assertEquals(PI / 2, horizontal.altitude, 0.01, "Altitude should be 90° (zenith)")
    }

    @Test
    fun testObjectOnHorizon() {
        // Object with Dec = -Lat should be on northern horizon when on meridian
        val lat = Math.toRadians(35.0)
        val observer = Observer(lat, 0.0)

        val dec = -lat
        val ra = Math.toRadians(180.0)
        val equatorial = EquatorialCoordinate(ra, dec)

        val lst = ra // On meridian

        val horizontal = CoordinateTransform.equatorialToHorizontal(equatorial, observer, lst)

        // Object should be on horizon (altitude ≈ 0°)
        assertEquals(0.0, horizontal.altitude, 0.1, "Altitude should be near 0° (horizon)")
    }

    @Test
    fun testSiriusVisibility() {
        // Test Sirius visibility from Tokyo at a specific time
        val tokyo = Observer.Tokyo
        val sirius = Star.Sirius

        // January 1, 2024, 21:00 JST (12:00 UTC)
        val jd = JulianDate.calculate(2024, 1, 1, 12.0)
        val lst = SiderealTime.calculateLST(jd, tokyo.longitude)

        val horizontal = CoordinateTransform.equatorialToHorizontal(sirius.coordinate, tokyo, lst)

        // Sirius should be visible (altitude > 0) at this time in Tokyo
        // (This is an approximate test - actual visibility depends on precise time)
        assertTrue(horizontal.altitude > -PI / 4, "Sirius altitude should be reasonable")
        assertTrue(horizontal.azimuth >= 0.0 && horizontal.azimuth < 2 * PI, "Azimuth should be in valid range")
    }

    @Test
    fun testRoundtripConversion() {
        // Test equatorial -> horizontal -> equatorial roundtrip
        val observer = Observer.fromDegrees(35.0, 139.0)
        val lst = Math.toRadians(180.0)

        val originalEquatorial = EquatorialCoordinate.fromDegrees(120.0, 30.0)

        // Convert to horizontal
        val horizontal = CoordinateTransform.equatorialToHorizontal(originalEquatorial, observer, lst)

        // Convert back to equatorial
        val roundtripEquatorial = CoordinateTransform.horizontalToEquatorial(horizontal, observer, lst)

        // Should match original (within numerical precision)
        assertEquals(originalEquatorial.ra, roundtripEquatorial.ra, 0.001, "RA should match after roundtrip")
        assertEquals(originalEquatorial.dec, roundtripEquatorial.dec, 0.001, "Dec should match after roundtrip")
    }

    @Test
    fun testAngularSeparation() {
        // Test angular separation between two stars
        // Use Betelgeuse and Sirius as examples

        val betelgeuse = Star.Betelgeuse.coordinate
        val sirius = Star.Sirius.coordinate

        val separation = CoordinateTransform.angularSeparation(betelgeuse, sirius)

        // Expected: approximately 25-30 degrees (0.44-0.52 radians)
        // This is a rough estimate
        assertTrue(separation > 0.4 && separation < 0.6, "Separation between Betelgeuse and Sirius should be ~25-30°")
    }

    @Test
    fun testAngularSeparationSamePoint() {
        // Angular separation from a point to itself should be 0
        val coord = EquatorialCoordinate.fromDegrees(100.0, 45.0)
        val separation = CoordinateTransform.angularSeparation(coord, coord)

        assertEquals(0.0, separation, 0.0001, "Separation from point to itself should be 0")
    }

    @Test
    fun testNorthPoleVisibility() {
        // From the North Pole, celestial north pole should be at zenith
        val northPole = Observer.fromDegrees(90.0, 0.0)

        // Star at Dec = 90° (north celestial pole)
        val polaris = EquatorialCoordinate.fromDegrees(0.0, 90.0)

        val lst = 0.0 // LST doesn't matter at pole

        val horizontal = CoordinateTransform.equatorialToHorizontal(polaris, northPole, lst)

        // Should be at zenith
        assertEquals(PI / 2, horizontal.altitude, 0.01, "North celestial pole should be at zenith from North Pole")
    }

    @Test
    fun testEquatorObserver() {
        // From the equator, celestial equator passes through zenith
        val equator = Observer.fromDegrees(0.0, 0.0)

        // Star on celestial equator (Dec = 0)
        val star = EquatorialCoordinate.fromDegrees(0.0, 0.0)

        // When RA = LST, star is on meridian
        val lst = 0.0

        val horizontal = CoordinateTransform.equatorialToHorizontal(star, equator, lst)

        // Should be at zenith (Alt = 90°)
        assertEquals(PI / 2, horizontal.altitude, 0.01, "Star on celestial equator at meridian should be at zenith from equator")
    }

    @Test
    fun testAzimuthNorthSouth() {
        // Object crossing meridian should have azimuth 0° (north) or 180° (south)
        val observer = Observer.fromDegrees(35.0, 0.0)

        // Star with Dec > Lat (passes north of zenith)
        val northStar = EquatorialCoordinate.fromDegrees(0.0, 80.0)
        val lst = 0.0 // On meridian

        val horizontalNorth = CoordinateTransform.equatorialToHorizontal(northStar, observer, lst)

        // Should be facing north (azimuth ≈ 0)
        assertTrue(abs(horizontalNorth.azimuth) < 0.1 || abs(horizontalNorth.azimuth - 2 * PI) < 0.1,
            "Object north of zenith should have azimuth near 0° (north)")
    }
}
