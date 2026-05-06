package com.astraloom.data.source

/**
 * Android implementation of ResourceReader
 *
 * Note: For Android, we'll use assets folder.
 * This requires a context, which will be provided during initialization.
 */
actual class ResourceReader {
    actual fun readResource(path: String): String {
        // For now, this is a placeholder implementation
        // In actual Android implementation, this would use:
        // context.assets.open(path).bufferedReader().use { it.readText() }

        // For testing purposes, return empty JSON
        return when (path) {
            "stars.json" -> """{"version":"1.0","description":"Star catalog","stars":[]}"""
            "constellations.json" -> """{"version":"1.0","description":"Constellation catalog","constellations":[]}"""
            else -> throw IllegalArgumentException("Resource not found: $path")
        }
    }
}
