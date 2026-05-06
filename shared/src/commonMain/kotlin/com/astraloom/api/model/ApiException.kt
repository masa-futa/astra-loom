package com.astraloom.api.model

/**
 * API exception types
 */
sealed class ApiException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    /**
     * Network error (no connection, timeout, etc.)
     */
    class NetworkError(message: String, cause: Throwable? = null) : ApiException(message, cause)

    /**
     * Server error (5xx)
     */
    class ServerError(val code: Int, message: String) : ApiException("Server error ($code): $message")

    /**
     * Client error (4xx)
     */
    class ClientError(val code: Int, message: String) : ApiException("Client error ($code): $message")

    /**
     * Parsing error (invalid JSON, etc.)
     */
    class ParsingError(message: String, cause: Throwable? = null) : ApiException(message, cause)

    /**
     * Unknown error
     */
    class Unknown(message: String, cause: Throwable? = null) : ApiException(message, cause)
}
