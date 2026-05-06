package com.astraloom.data.source

/**
 * Platform-specific resource reader
 * (プラットフォーム固有のリソース読み込み)
 *
 * This is an expect/actual pattern for KMP.
 * Each platform provides its own implementation.
 */
expect class ResourceReader() {
    /**
     * Read a resource file as a string
     *
     * @param path Resource file path (e.g., "stars.json")
     * @return File contents as string
     */
    fun readResource(path: String): String
}
