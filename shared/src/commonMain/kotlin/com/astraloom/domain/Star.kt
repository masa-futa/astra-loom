package com.astraloom.domain

/**
 * Star entity (恒星)
 *
 * @property id Unique identifier (e.g., HIP number, Bayer designation)
 * @property name Common name (e.g., "Sirius", "Betelgeuse")
 * @property coordinate Equatorial coordinate (J2000)
 * @property magnitude Visual magnitude (明るさ) - lower is brighter
 * @property spectralType Spectral classification (e.g., "A1V", "M2III")
 */
data class Star(
    val id: String,
    val name: String? = null,
    val coordinate: EquatorialCoordinate,
    val magnitude: Double,
    val spectralType: String? = null
) {
    /**
     * Check if the star is visible to naked eye
     * Generally, magnitude < 6.0 is visible under dark skies
     */
    fun isVisibleToNakedEye(): Boolean = magnitude < 6.0

    /**
     * Check if the star is bright (for MVP, we focus on bright stars)
     */
    fun isBright(): Boolean = magnitude < 4.0

    companion object {
        /**
         * Example: Sirius (brightest star in the night sky)
         * RA: 6h 45m 8.9s, Dec: -16° 42' 58"
         * Magnitude: -1.46
         */
        val Sirius = Star(
            id = "HIP32349",
            name = "Sirius",
            coordinate = EquatorialCoordinate.fromHMS_DMS(
                raHours = 6,
                raMinutes = 45,
                raSeconds = 8.9,
                decDegrees = -16,
                decArcMinutes = 42,
                decArcSeconds = 58.0
            ),
            magnitude = -1.46,
            spectralType = "A1V"
        )

        /**
         * Example: Betelgeuse (red supergiant in Orion)
         * RA: 5h 55m 10.3s, Dec: +7° 24' 25"
         * Magnitude: 0.42 (variable)
         */
        val Betelgeuse = Star(
            id = "HIP27989",
            name = "Betelgeuse",
            coordinate = EquatorialCoordinate.fromHMS_DMS(
                raHours = 5,
                raMinutes = 55,
                raSeconds = 10.3,
                decDegrees = 7,
                decArcMinutes = 24,
                decArcSeconds = 25.0
            ),
            magnitude = 0.42,
            spectralType = "M2Iab"
        )
    }
}
