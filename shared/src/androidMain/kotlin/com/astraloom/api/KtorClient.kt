package com.astraloom.api

import io.ktor.client.engine.*
import io.ktor.client.engine.android.*

/**
 * Android implementation using Android engine
 */
actual fun createHttpClientEngine(): HttpClientEngine {
    return Android.create()
}
