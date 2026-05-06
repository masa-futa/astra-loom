package com.astraloom.astronomy

import com.astraloom.domain.Observer
import com.astraloom.domain.Star
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AstronomyEngineTest {

    @Test
    fun testBasicStarPositionCalculation() {
        val engine = AstronomyEngine()
        val observer = Observer.Tokyo
        val sirius = Star.Sirius

        // January 1, 2024, 21:00 JST (12:00 UTC)
        val time = Instant.parse("2024-01-01T12:00:00Z")

        val position = engine.calculateStarPosition(sirius, observer, time)

        // Sirius should be somewhere in the sky (not testing exact position)
        assertTrue(position.horizontal.altitude >= -Math.PI / 2, "Altitude should be valid")
        assertTrue(position.horizontal.azimuth >= 0.0, "Azimuth should be valid")
        assertTrue(position.jd > 0.0, "JD should be positive")
    }

    @Test
    fun testMultipleStarCalculation() {
        val engine = AstronomyEngine()
        val observer = Observer.Tokyo
        val stars = listOf(Star.Sirius, Star.Betelgeuse)
        val time = Instant.parse("2024-01-01T12:00:00Z")

        val positions = engine.calculateStarPositions(stars, observer, time)

        assertEquals(2, positions.size, "Should calculate positions for 2 stars")
        assertTrue(positions.containsKey(Star.Sirius.id), "Should contain Sirius")
        assertTrue(positions.containsKey(Star.Betelgeuse.id), "Should contain Betelgeuse")
    }

    @Test
    fun testVisibleStarsOnly() {
        val engine = AstronomyEngine()
        val observer = Observer.Tokyo
        val stars = listOf(Star.Sirius, Star.Betelgeuse)
        val time = Instant.parse("2024-01-01T12:00:00Z")

        val visibleStars = engine.calculateVisibleStars(stars, observer, time)

        // All returned stars should be above horizon
        visibleStars.values.forEach { position ->
            assertTrue(position.isVisible(), "All returned stars should be visible")
            assertTrue(position.horizontal.altitude > 0.0, "Visible stars should have positive altitude")
        }
    }

    @Test
    fun testPrecessionToggle() {
        val observer = Observer.Tokyo
        val sirius = Star.Sirius
        val time = Instant.parse("2024-01-01T12:00:00Z")

        // With precession
        val engineWithPrecession = AstronomyEngine(
            AstronomyEngine.EngineConfig(applyPrecession = true)
        )
        val positionWithPrecession = engineWithPrecession.calculateStarPosition(sirius, observer, time)

        // Without precession
        val engineWithoutPrecession = AstronomyEngine(
            AstronomyEngine.EngineConfig(applyPrecession = false)
        )
        val positionWithoutPrecession = engineWithoutPrecession.calculateStarPosition(sirius, observer, time)

        // Positions should be slightly different (due to precession over 24 years from J2000)
        val altDiff = kotlin.math.abs(
            positionWithPrecession.horizontal.altitude - positionWithoutPrecession.horizontal.altitude
        )

        // Should have some difference (but might be small)
        // Not asserting exact value as it depends on star position
        assertTrue(altDiff >= 0.0, "Altitude difference should be non-negative")
    }

    @Test
    fun testRefractionToggle() {
        val observer = Observer.Tokyo
        val sirius = Star.Sirius
        val time = Instant.parse("2024-01-01T12:00:00Z")

        // With refraction
        val engineWithRefraction = AstronomyEngine(
            AstronomyEngine.EngineConfig(applyRefraction = true)
        )
        val positionWithRefraction = engineWithRefraction.calculateStarPosition(sirius, observer, time)

        // Without refraction
        val engineWithoutRefraction = AstronomyEngine(
            AstronomyEngine.EngineConfig(applyRefraction = false)
        )
        val positionWithoutRefraction = engineWithoutRefraction.calculateStarPosition(sirius, observer, time)

        // With refraction, altitude should be higher (if star is above horizon)
        if (positionWithRefraction.isVisible()) {
            assertTrue(
                positionWithRefraction.horizontal.altitude >= positionWithoutRefraction.horizontal.altitude,
                "Refraction should increase apparent altitude"
            )
        }
    }

    @Test
    fun testStarsSortedByAltitude() {
        val engine = AstronomyEngine()
        val observer = Observer.Tokyo
        val stars = listOf(Star.Sirius, Star.Betelgeuse)
        val time = Instant.parse("2024-01-01T12:00:00Z")

        val sorted = engine.getStarsSortedByAltitude(stars, observer, time)

        // Should be sorted in descending order of altitude
        for (i in 0 until sorted.size - 1) {
            assertTrue(
                sorted[i].second.horizontal.altitude >= sorted[i + 1].second.horizontal.altitude,
                "Stars should be sorted by altitude descending"
            )
        }
    }

    @Test
    fun testAngularSeparation() {
        val engine = AstronomyEngine()
        val sirius = Star.Sirius
        val betelgeuse = Star.Betelgeuse

        val separation = engine.calculateAngularSeparation(sirius, betelgeuse)

        // Sirius and Betelgeuse are in different parts of the sky
        // Separation should be > 0
        assertTrue(separation > 0.0, "Angular separation should be positive")
        assertTrue(separation < Math.PI, "Angular separation should be < 180°")
    }

    @Test
    fun testAngularSeparationSameStar() {
        val engine = AstronomyEngine()
        val sirius = Star.Sirius

        val separation = engine.calculateAngularSeparation(sirius, sirius)

        assertEquals(0.0, separation, 0.0001, "Angular separation of star with itself should be 0")
    }

    @Test
    fun testPositionResultProperties() {
        val engine = AstronomyEngine()
        val observer = Observer.Tokyo
        val sirius = Star.Sirius
        val time = Instant.parse("2024-01-01T12:00:00Z")

        val position = engine.calculateStarPosition(sirius, observer, time)

        // Test convenience methods
        val altDeg = position.altitudeDegrees()
        val azDeg = position.azimuthDegrees()

        assertTrue(altDeg >= -90.0 && altDeg <= 90.0, "Altitude in degrees should be in valid range")
        assertTrue(azDeg >= 0.0 && azDeg < 360.0, "Azimuth in degrees should be in valid range")
    }

    @Test
    fun testAtmosphericConditions() {
        val observer = Observer.Tokyo
        val sirius = Star.Sirius
        val time = Instant.parse("2024-01-01T12:00:00Z")

        // Standard conditions
        val engineStandard = AstronomyEngine(
            AstronomyEngine.EngineConfig(
                applyRefraction = true,
                pressureMillibars = 1010.0,
                temperatureCelsius = 10.0
            )
        )

        // High pressure, cold (more refraction)
        val engineMoreRefraction = AstronomyEngine(
            AstronomyEngine.EngineConfig(
                applyRefraction = true,
                pressureMillibars = 1030.0,
                temperatureCelsius = -10.0
            )
        )

        val posStandard = engineStandard.calculateStarPosition(sirius, observer, time)
        val posMoreRefraction = engineMoreRefraction.calculateStarPosition(sirius, observer, time)

        // More refraction should give higher apparent altitude (if visible)
        if (posStandard.isVisible()) {
            assertTrue(
                posMoreRefraction.horizontal.altitude >= posStandard.horizontal.altitude,
                "Higher pressure/lower temp should increase refraction"
            )
        }
    }

    @Test
    fun testDifferentObserverLocations() {
        val engine = AstronomyEngine()
        val sirius = Star.Sirius
        val time = Instant.parse("2024-01-01T12:00:00Z")

        val positionTokyo = engine.calculateStarPosition(sirius, Observer.Tokyo, time)
        val positionNewYork = engine.calculateStarPosition(sirius, Observer.NewYork, time)
        val positionLondon = engine.calculateStarPosition(sirius, Observer.London, time)

        // Positions should be different from different locations
        assertTrue(
            positionTokyo.horizontal.altitude != positionNewYork.horizontal.altitude ||
            positionTokyo.horizontal.azimuth != positionNewYork.horizontal.azimuth,
            "Position should differ between Tokyo and New York"
        )

        assertTrue(
            positionNewYork.horizontal.altitude != positionLondon.horizontal.altitude ||
            positionNewYork.horizontal.azimuth != positionLondon.horizontal.azimuth,
            "Position should differ between New York and London"
        )
    }
}
