package com.astraloom.data.model

import com.astraloom.domain.EquatorialCoordinate
import com.astraloom.domain.Star
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for Star
 * Used for JSON serialization/deserialization
 */
@Serializable
data class StarDto(
    val id: String,
    val name: String? = null,
    val raDegrees: Double,  // Right Ascension in degrees
    val decDegrees: Double, // Declination in degrees
    val magnitude: Double,
    val spectralType: String? = null
) {
    /**
     * Convert DTO to domain model
     */
    fun toDomain(): Star {
        return Star(
            id = id,
            name = name,
            coordinate = EquatorialCoordinate.fromDegrees(raDegrees, decDegrees),
            magnitude = magnitude,
            spectralType = spectralType
        )
    }

    companion object {
        /**
         * Convert domain model to DTO
         */
        fun fromDomain(star: Star): StarDto {
            return StarDto(
                id = star.id,
                name = star.name,
                raDegrees = star.coordinate.raToHours() * 15.0, // Hours to degrees
                decDegrees = star.coordinate.decToDegrees(),
                magnitude = star.magnitude,
                spectralType = star.spectralType
            )
        }
    }
}

/**
 * Container for star catalog JSON
 */
@Serializable
data class StarCatalogDto(
    val version: String,
    val description: String,
    val stars: List<StarDto>
)
