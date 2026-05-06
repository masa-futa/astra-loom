package com.astraloom.domain

/**
 * Equatorial coordinate system (赤道座標系)
 * J2000.0 epoch
 *
 * @property ra Right Ascension in radians (赤経) [0, 2π]
 * @property dec Declination in radians (赤緯) [-π/2, π/2]
 */
data class EquatorialCoordinate(
    val ra: Double,
    val dec: Double
) {
    init {
        require(ra in 0.0..TWO_PI) { "RA must be in range [0, 2π], got $ra" }
        require(dec in -HALF_PI..HALF_PI) { "Dec must be in range [-π/2, π/2], got $dec" }
    }

    companion object {
        private const val TWO_PI = 2 * Math.PI
        private const val HALF_PI = Math.PI / 2

        /**
         * Create from hours, minutes, seconds (RA) and degrees, arcminutes, arcseconds (Dec)
         */
        fun fromHMS_DMS(
            raHours: Int,
            raMinutes: Int,
            raSeconds: Double,
            decDegrees: Int,
            decArcMinutes: Int,
            decArcSeconds: Double
        ): EquatorialCoordinate {
            // RA: hours to radians
            val raInHours = raHours + raMinutes / 60.0 + raSeconds / 3600.0
            val raRadians = raInHours * (Math.PI / 12.0) // 24 hours = 2π radians

            // Dec: degrees to radians
            val decSign = if (decDegrees < 0) -1.0 else 1.0
            val decInDegrees = Math.abs(decDegrees) + decArcMinutes / 60.0 + decArcSeconds / 3600.0
            val decRadians = decSign * Math.toRadians(decInDegrees)

            return EquatorialCoordinate(raRadians, decRadians)
        }

        /**
         * Create from degrees
         */
        fun fromDegrees(raDegrees: Double, decDegrees: Double): EquatorialCoordinate {
            return EquatorialCoordinate(
                Math.toRadians(raDegrees),
                Math.toRadians(decDegrees)
            )
        }
    }

    /**
     * Convert RA to hours
     */
    fun raToHours(): Double = ra * (12.0 / Math.PI)

    /**
     * Convert Dec to degrees
     */
    fun decToDegrees(): Double = Math.toDegrees(dec)
}
