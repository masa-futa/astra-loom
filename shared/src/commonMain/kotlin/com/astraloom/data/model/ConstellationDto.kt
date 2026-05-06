package com.astraloom.data.model

import com.astraloom.domain.Constellation
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for Constellation
 * Used for JSON serialization/deserialization
 */
@Serializable
data class ConstellationDto(
    val id: String,
    val name: String,
    val starIds: List<String>,
    val lines: List<LineDto>
) {
    @Serializable
    data class LineDto(
        val from: String,
        val to: String
    )

    /**
     * Convert DTO to domain model
     */
    fun toDomain(): Constellation {
        return Constellation(
            id = id,
            name = name,
            starIds = starIds,
            lines = lines.map { it.from to it.to }
        )
    }

    companion object {
        /**
         * Convert domain model to DTO
         */
        fun fromDomain(constellation: Constellation): ConstellationDto {
            return ConstellationDto(
                id = constellation.id,
                name = constellation.name,
                starIds = constellation.starIds,
                lines = constellation.lines.map { (from, to) ->
                    LineDto(from, to)
                }
            )
        }
    }
}

/**
 * Container for constellation catalog JSON
 */
@Serializable
data class ConstellationCatalogDto(
    val version: String,
    val description: String,
    val constellations: List<ConstellationDto>
)
