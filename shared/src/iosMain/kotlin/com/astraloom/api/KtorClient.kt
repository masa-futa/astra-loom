package com.astraloom.api

import io.ktor.client.engine.*
import io.ktor.client.engine.darwin.*

/**
 * iOS implementation using Darwin engine
 */
actual fun createHttpClientEngine(): HttpClientEngine {
    return Darwin.create()
}
