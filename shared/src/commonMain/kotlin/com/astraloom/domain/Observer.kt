package com.astraloom.domain

/**
 * Observer location on Earth (観測地点)
 *
 * @property latitude Latitude in radians (緯度) [-π/2, π/2]
 *                    Positive = North, Negative = South
 * @property longitude Longitude in radians (経度) [-π, π]
 *                     Positive = East, Negative = West
 * @property elevation Elevation above sea level in meters (標高)
 */
data class Observer(
    val latitude: Double,
    val longitude: Double,
    val elevation: Double = 0.0
) {
    init {
        require(latitude in -HALF_PI..HALF_PI) { "Latitude must be in range [-π/2, π/2], got $latitude" }
        require(longitude in -Math.PI..Math.PI) { "Longitude must be in range [-π, π], got $longitude" }
        require(elevation >= -500.0) { "Elevation must be >= -500m (Dead Sea), got $elevation" }
    }

    companion object {
        private const val HALF_PI = Math.PI / 2

        /**
         * Create Observer from degrees
         */
        fun fromDegrees(
            latitudeDegrees: Double,
            longitudeDegrees: Double,
            elevationMeters: Double = 0.0
        ): Observer {
            return Observer(
                Math.toRadians(latitudeDegrees),
                Math.toRadians(longitudeDegrees),
                elevationMeters
            )
        }

        /**
         * Example locations
         */
        val Tokyo = fromDegrees(35.6762, 139.6503, 40.0)
        val NewYork = fromDegrees(40.7128, -74.0060, 10.0)
        val London = fromDegrees(51.5074, -0.1278, 11.0)
    }

    /**
     * Convert latitude to degrees
     */
    fun latitudeToDegrees(): Double = Math.toDegrees(latitude)

    /**
     * Convert longitude to degrees
     */
    fun longitudeToDegrees(): Double = Math.toDegrees(longitude)
}
