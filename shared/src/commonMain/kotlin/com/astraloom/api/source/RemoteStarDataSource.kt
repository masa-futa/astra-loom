package com.astraloom.api.source

import com.astraloom.api.ApiEndpoints
import com.astraloom.api.model.ApiException
import com.astraloom.api.model.ApiResponse
import com.astraloom.data.model.StarCatalogDto
import com.astraloom.data.model.StarDto
import com.astraloom.domain.Star
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Remote data source for stars using API
 * (APIを使用したリモート星データソース)
 */
class RemoteStarDataSource(
    private val httpClient: HttpClient
) {
    /**
     * Fetch all stars from API
     */
    suspend fun fetchStars(): Result<List<Star>> {
        return executeRequest {
            val response: ApiResponse<StarCatalogDto> = httpClient.get(ApiEndpoints.Stars.ALL).body()
            response.data?.stars?.map { it.toDomain() } ?: emptyList()
        }
    }

    /**
     * Fetch star by ID
     */
    suspend fun fetchStarById(id: String): Result<Star?> {
        return executeRequest {
            val response: ApiResponse<StarDto> = httpClient.get(ApiEndpoints.Stars.BY_ID.replace("{id}", id)).body()
            response.data?.toDomain()
        }
    }

    /**
     * Fetch bright stars (magnitude < 4.0)
     */
    suspend fun fetchBrightStars(): Result<List<Star>> {
        return executeRequest {
            val response: ApiResponse<List<StarDto>> = httpClient.get(ApiEndpoints.Stars.BRIGHT).body()
            response.data?.map { it.toDomain() } ?: emptyList()
        }
    }

    /**
     * Search stars by name
     */
    suspend fun searchStars(query: String): Result<List<Star>> {
        return executeRequest {
            val response: ApiResponse<List<StarDto>> = httpClient.get(ApiEndpoints.Stars.SEARCH) {
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
