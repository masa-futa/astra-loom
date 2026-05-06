package com.astraloom.data.repository

import com.astraloom.data.source.LocalConstellationDataSource
import com.astraloom.domain.Constellation
import com.astraloom.domain.Star
import com.astraloom.repository.ConstellationRepository
import com.astraloom.repository.StarRepository

/**
 * Local implementation of ConstellationRepository using embedded JSON data
 */
class LocalConstellationRepository(
    private val dataSource: LocalConstellationDataSource,
    private val starRepository: StarRepository
) : ConstellationRepository {

    override suspend fun getAllConstellations(): Result<List<Constellation>> {
        return dataSource.loadConstellations()
    }

    override suspend fun getConstellationById(id: String): Result<Constellation?> {
        return getAllConstellations().map { constellations ->
            constellations.find { it.id == id }
        }
    }

    override suspend fun getConstellationByName(name: String): Result<Constellation?> {
        return getAllConstellations().map { constellations ->
            constellations.find { it.name.equals(name, ignoreCase = true) }
        }
    }

    override suspend fun getStarsInConstellation(constellationId: String): Result<List<Star>> {
        return getConstellationById(constellationId).mapCatching { constellation ->
            if (constellation == null) {
                emptyList()
            } else {
                starRepository.getStarsByIds(constellation.starIds)
                    .getOrThrow()
            }
        }
    }

    override suspend fun getConstellationLines(constellationId: String): Result<List<Pair<String, String>>> {
        return getConstellationById(constellationId).map { constellation ->
            constellation?.lines ?: emptyList()
        }
    }

    override suspend fun searchConstellationsByName(query: String): Result<List<Constellation>> {
        return getAllConstellations().map { constellations ->
            constellations.filter { constellation ->
                constellation.name.contains(query, ignoreCase = true) ||
                constellation.id.contains(query, ignoreCase = true)
            }
        }
    }

    override suspend fun getMajorConstellations(): Result<List<Constellation>> {
        // Major constellations (well-known ones)
        val majorIds = setOf(
            "Ori", // Orion
            "UMa", // Ursa Major (Big Dipper)
            "UMi", // Ursa Minor (Little Dipper)
            "Cas", // Cassiopeia
            "Leo", // Leo
            "Sco", // Scorpius
            "Gem", // Gemini
            "Tau", // Taurus
            "Aql", // Aquila
            "Cyg"  // Cygnus
        )

        return getAllConstellations().map { constellations ->
            constellations.filter { it.id in majorIds }
        }
    }

    override suspend fun refresh(): Result<Unit> {
        return try {
            dataSource.clearCache()
            dataSource.loadConstellations().map { }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
