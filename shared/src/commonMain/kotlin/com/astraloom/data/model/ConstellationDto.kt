package com.astraloom.data.model

import com.astraloom.domain.Constellation
import com.astraloom.domain.Season
import com.astraloom.domain.Hemisphere
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for Constellation
 * Used for JSON serialization/deserialization
 */
@Serializable
data class ConstellationDto(
    val id: String,
    val name: String,
    val nameJa: String,
    val starIds: List<String>,
    val lines: List<LineDto>,
    val season: String? = null,
    val hemisphere: String? = null,
    val mythology: String? = null,
    val description: String? = null,
    val findingTips: String? = null
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
            nameJa = nameJa,
            starIds = starIds,
            lines = lines.map { it.from to it.to },
            season = season?.let { Season.valueOf(it.uppercase()) },
            hemisphere = hemisphere?.let { Hemisphere.valueOf(it.uppercase()) },
            mythology = mythology,
            description = description,
            findingTips = findingTips
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
                nameJa = constellation.nameJa,
                starIds = constellation.starIds,
                lines = constellation.lines.map { (from, to) ->
                    LineDto(from, to)
                },
                season = constellation.season?.name?.lowercase(),
                hemisphere = constellation.hemisphere?.name?.lowercase(),
                mythology = constellation.mythology,
                description = constellation.description,
                findingTips = constellation.findingTips
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
