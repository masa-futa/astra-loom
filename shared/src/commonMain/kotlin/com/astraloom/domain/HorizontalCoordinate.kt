package com.astraloom.domain

/**
 * Horizontal coordinate system (地平座標系)
 * Also known as Alt-Azimuth system
 *
 * @property altitude Altitude angle in radians (高度) [-π/2, π/2]
 * @property azimuth Azimuth angle in radians (方位角) [0, 2π]
 *                   0 = North, π/2 = East, π = South, 3π/2 = West
 */
data class HorizontalCoordinate(
    val altitude: Double,
    val azimuth: Double
) {
    init {
        require(altitude in -HALF_PI..HALF_PI) { "Altitude must be in range [-π/2, π/2], got $altitude" }
        require(azimuth in 0.0..TWO_PI) { "Azimuth must be in range [0, 2π], got $azimuth" }
    }

    companion object {
        private const val TWO_PI = 2 * Math.PI
        private const val HALF_PI = Math.PI / 2

        /**
         * Create from degrees
         */
        fun fromDegrees(altitudeDegrees: Double, azimuthDegrees: Double): HorizontalCoordinate {
            return HorizontalCoordinate(
                Math.toRadians(altitudeDegrees),
                Math.toRadians(azimuthDegrees)
            )
        }
    }

    /**
     * Convert altitude to degrees
     */
    fun altitudeToDegrees(): Double = Math.toDegrees(altitude)

    /**
     * Convert azimuth to degrees
     */
    fun azimuthToDegrees(): Double = Math.toDegrees(azimuth)

    /**
     * Check if the object is visible (above horizon)
     */
    fun isVisible(): Boolean = altitude > 0.0
}
