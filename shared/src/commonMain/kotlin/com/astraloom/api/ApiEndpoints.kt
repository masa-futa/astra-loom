package com.astraloom.api

/**
 * API endpoint definitions
 * (APIエンドポイント定義)
 */
object ApiEndpoints {
    // Base paths
    const val STARS = "stars"
    const val CONSTELLATIONS = "constellations"

    // Star endpoints
    object Stars {
        const val ALL = STARS
        const val BY_ID = "$STARS/{id}"
        const val BY_MAGNITUDE = "$STARS/magnitude"
        const val BRIGHT = "$STARS/bright"
        const val SEARCH = "$STARS/search"
    }

    // Constellation endpoints
    object Constellations {
        const val ALL = CONSTELLATIONS
        const val BY_ID = "$CONSTELLATIONS/{id}"
        const val MAJOR = "$CONSTELLATIONS/major"
        const val SEARCH = "$CONSTELLATIONS/search"
        const val STARS = "$CONSTELLATIONS/{id}/stars"
    }
}
