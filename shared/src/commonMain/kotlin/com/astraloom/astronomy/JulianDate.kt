package com.astraloom.astronomy

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.floor

/**
 * Julian Date calculations (ユリウス日計算)
 *
 * Julian Date (JD) is a continuous count of days since the beginning of
 * the Julian Period (January 1, 4713 BC, noon UTC).
 *
 * Reference: Meeus, "Astronomical Algorithms"
 */
object JulianDate {
    /**
     * Standard epoch J2000.0
     * January 1, 2000, 12:00 TT (Terrestrial Time)
     */
    const val J2000 = 2451545.0

    /**
     * Calculate Julian Date from calendar date and time
     *
     * Formula:
     * JD = 367Y - floor(7(Y + floor((M+9)/12)) / 4) + floor(275M/9) + D + 1721013.5 + (UT / 24)
     *
     * @param year Year (e.g., 2024)
     * @param month Month (1-12)
     * @param day Day (1-31)
     * @param hour Hour in decimal (0.0-24.0), UTC
     * @return Julian Date
     */
    fun calculate(
        year: Int,
        month: Int,
        day: Int,
        hour: Double = 0.0
    ): Double {
        require(month in 1..12) { "Month must be 1-12, got $month" }
        require(day in 1..31) { "Day must be 1-31, got $day" }
        require(hour in 0.0..24.0) { "Hour must be 0-24, got $hour" }

        // Adjust year and month for January and February
        val y = if (month <= 2) year - 1 else year
        val m = if (month <= 2) month + 12 else month

        // Gregorian calendar correction
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)

        val jd = floor(365.25 * (y + 4716)) +
                floor(30.6001 * (m + 1)) +
                day + b - 1524.5 + (hour / 24.0)

        return jd
    }

    /**
     * Calculate Julian Date from Instant
     *
     * @param instant Instant (UTC)
     * @return Julian Date
     */
    fun fromInstant(instant: Instant): Double {
        val dt = instant.toLocalDateTime(TimeZone.UTC)
        val hour = dt.hour + dt.minute / 60.0 + dt.second / 3600.0 + dt.nanosecond / 3_600_000_000_000.0
        return calculate(dt.year, dt.monthNumber, dt.dayOfMonth, hour)
    }

    /**
     * Calculate number of centuries since J2000.0
     *
     * T = (JD - J2000.0) / 36525.0
     *
     * This is used in many astronomical calculations
     *
     * @param jd Julian Date
     * @return Centuries since J2000.0
     */
    fun centuriesSinceJ2000(jd: Double): Double {
        return (jd - J2000) / 36525.0
    }

    /**
     * Calculate Modified Julian Date (MJD)
     * MJD = JD - 2400000.5
     *
     * MJD is often used in modern astronomy to avoid large numbers
     *
     * @param jd Julian Date
     * @return Modified Julian Date
     */
    fun toMJD(jd: Double): Double {
        return jd - 2400000.5
    }

    /**
     * Convert Modified Julian Date to Julian Date
     *
     * @param mjd Modified Julian Date
     * @return Julian Date
     */
    fun fromMJD(mjd: Double): Double {
        return mjd + 2400000.5
    }
}
