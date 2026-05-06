package com.astraloom.domain

/**
 * Constellation (星座)
 *
 * @property id IAU constellation abbreviation (e.g., "Ori" for Orion)
 * @property name Full name (e.g., "Orion")
 * @property starIds List of star IDs that form this constellation
 * @property lines List of star ID pairs that form constellation lines
 */
data class Constellation(
    val id: String,
    val name: String,
    val starIds: List<String>,
    val lines: List<Pair<String, String>>
) {
    companion object {
        /**
         * Example: Orion (simplified)
         */
        val Orion = Constellation(
            id = "Ori",
            name = "Orion",
            starIds = listOf("HIP27989", "HIP25336", "HIP26311"), // Betelgeuse, Bellatrix, Rigel (simplified)
            lines = listOf(
                "HIP27989" to "HIP25336", // Betelgeuse to Bellatrix
                "HIP25336" to "HIP26311"  // Bellatrix to Rigel
            )
        )
    }
}
