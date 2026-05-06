package com.astraloom.api

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Ktor HttpClient wrapper for API calls
 * (API呼び出し用のKtor HttpClientラッパー)
 */
object KtorClient {

    /**
     * Create configured HttpClient
     *
     * @param config Optional client configuration
     * @return Configured HttpClient
     */
    fun create(config: ClientConfig = ClientConfig()): HttpClient {
        return HttpClient(getHttpClientEngine()) {
            // JSON content negotiation
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = config.prettyPrintJson
                })
            }

            // Logging
            install(Logging) {
                logger = Logger.DEFAULT
                level = if (config.enableLogging) LogLevel.INFO else LogLevel.NONE
            }

            // Timeout configuration
            install(HttpTimeout) {
                requestTimeoutMillis = config.requestTimeoutMs
                connectTimeoutMillis = config.connectTimeoutMs
                socketTimeoutMillis = config.socketTimeoutMs
            }

            // Default request configuration
            defaultRequest {
                url(config.baseUrl)
            }

            // Retry on failure (optional)
            if (config.enableRetry) {
                install(HttpRequestRetry) {
                    retryOnServerErrors(maxRetries = config.maxRetries)
                    exponentialDelay()
                }
            }
        }
    }

    /**
     * Platform-specific HTTP engine
     * This is an expect/actual pattern
     */
    private fun getHttpClientEngine() = createHttpClientEngine()
}

/**
 * Client configuration
 */
data class ClientConfig(
    val baseUrl: String = "https://api.astraloom.example.com/v1/",
    val requestTimeoutMs: Long = 30_000,
    val connectTimeoutMs: Long = 10_000,
    val socketTimeoutMs: Long = 30_000,
    val enableLogging: Boolean = true,
    val prettyPrintJson: Boolean = false,
    val enableRetry: Boolean = true,
    val maxRetries: Int = 3
)

/**
 * Platform-specific HTTP client engine
 */
expect fun createHttpClientEngine(): io.ktor.client.engine.HttpClientEngine
