package com.astraloom.api.model

import kotlinx.serialization.Serializable

/**
 * Generic API response wrapper
 */
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ApiError? = null,
    val timestamp: Long = 0
)

/**
 * API error model
 */
@Serializable
data class ApiError(
    val code: String,
    val message: String,
    val details: String? = null
)

/**
 * Paginated response
 */
@Serializable
data class PaginatedResponse<T>(
    val items: List<T>,
    val total: Int,
    val page: Int,
    val pageSize: Int,
    val hasNext: Boolean
)
