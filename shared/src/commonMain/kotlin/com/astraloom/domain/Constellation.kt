package com.astraloom.domain

/**
 * Constellation (星座)
 *
 * @property id IAU constellation abbreviation (e.g., "Ori" for Orion)
 * @property name Full name in English (e.g., "Orion")
 * @property nameJa Japanese name (e.g., "オリオン座")
 * @property starIds List of star IDs that form this constellation
 * @property lines List of star ID pairs that form constellation lines
 * @property season Best season for observation
 * @property hemisphere Primary hemisphere for visibility
 * @property mythology Mythology and origin story
 * @property description Description of the constellation
 * @property findingTips Tips for finding this constellation
 */
data class Constellation(
    val id: String,
    val name: String,
    val nameJa: String,
    val starIds: List<String>,
    val lines: List<Pair<String, String>>,
    val season: Season? = null,
    val hemisphere: Hemisphere? = null,
    val mythology: String? = null,
    val description: String? = null,
    val findingTips: String? = null
) {
    companion object {
        /**
         * Example: Orion (simplified)
         */
        val Orion = Constellation(
            id = "Ori",
            name = "Orion",
            nameJa = "オリオン座",
            starIds = listOf("HIP27989", "HIP25336", "HIP26311"), // Betelgeuse, Bellatrix, Rigel (simplified)
            lines = listOf(
                "HIP27989" to "HIP25336", // Betelgeuse to Bellatrix
                "HIP25336" to "HIP26311"  // Bellatrix to Rigel
            ),
            season = Season.WINTER,
            hemisphere = Hemisphere.EQUATORIAL
        )
    }
}

/**
 * Season for constellation observation
 * (星座観測に適した季節)
 */
enum class Season {
    SPRING,   // 春
    SUMMER,   // 夏
    AUTUMN,   // 秋
    WINTER,   // 冬
    ALL_YEAR  // 通年
}

/**
 * Hemisphere for constellation visibility
 * (星座が主に見える半球)
 */
enum class Hemisphere {
    NORTHERN,    // 北半球
    SOUTHERN,    // 南半球
    EQUATORIAL   // 赤道付近（両半球）
}
