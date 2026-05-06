package com.astraloom.api.source

import com.astraloom.api.ApiEndpoints
import com.astraloom.api.model.ApiException
import com.astraloom.api.model.ApiResponse
import com.astraloom.data.model.ConstellationCatalogDto
import com.astraloom.data.model.ConstellationDto
import com.astraloom.domain.Constellation
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

/**
 * Remote data source for constellations using API
 * (APIを使用したリモート星座データソース)
 */
class RemoteConstellationDataSource(
    private val httpClient: HttpClient
) {
    /**
     * Fetch all constellations from API
     */
    suspend fun fetchConstellations(): Result<List<Constellation>> {
        return executeRequest {
            val response: ApiResponse<ConstellationCatalogDto> =
                httpClient.get(ApiEndpoints.Constellations.ALL).body()
            response.data?.constellations?.map { it.toDomain() } ?: emptyList()
        }
    }

    /**
     * Fetch constellation by ID
     */
    suspend fun fetchConstellationById(id: String): Result<Constellation?> {
        return executeRequest {
            val response: ApiResponse<ConstellationDto> =
                httpClient.get(ApiEndpoints.Constellations.BY_ID.replace("{id}", id)).body()
            response.data?.toDomain()
        }
    }

    /**
     * Fetch major constellations
     */
    suspend fun fetchMajorConstellations(): Result<List<Constellation>> {
        return executeRequest {
            val response: ApiResponse<List<ConstellationDto>> =
                httpClient.get(ApiEndpoints.Constellations.MAJOR).body()
            response.data?.map { it.toDomain() } ?: emptyList()
        }
    }

    /**
     * Search constellations by name
     */
    suspend fun searchConstellations(query: String): Result<List<Constellation>> {
        return executeRequest {
            val response: ApiResponse<List<ConstellationDto>> =
                httpClient.get(ApiEndpoints.Constellations.SEARCH) {
                    parameter("q", query)
                }.body()
            response.data?.map { it.toDomain() } ?: emptyList()
        }
    }

    /**
     * Execute HTTP request with error handling
     */
    private suspend fun <T> executeRequest(block: suspend () -> T): Result<T> {
        return try {
            Result.success(block())
        } catch (e: Exception) {
            Result.failure(mapException(e))
        }
    }

    /**
     * Map HTTP exceptions to ApiException
     */
    private fun mapException(e: Exception): ApiException {
        return when (e) {
            is io.ktor.client.network.sockets.SocketTimeoutException ->
                ApiException.NetworkError("Request timeout", e)
            is io.ktor.client.plugins.ClientRequestException ->
                ApiException.ClientError(e.response.status.value, e.message)
            is io.ktor.client.plugins.ServerResponseException ->
                ApiException.ServerError(e.response.status.value, e.message)
            else ->
                ApiException.Unknown("Unknown error: ${e.message}", e)
        }
    }
}
