package com.astraloom.data.cache

/**
 * Cache strategy enumeration
 * (キャッシュ戦略)
 */
enum class CacheStrategy {
    /**
     * Cache first, then network if cache miss
     * (キャッシュ優先、キャッシュミス時はネットワーク)
     */
    CACHE_FIRST,

    /**
     * Network first, fallback to cache on error
     * (ネットワーク優先、エラー時はキャッシュにフォールバック)
     */
    NETWORK_FIRST,

    /**
     * Cache only, no network requests
     * (キャッシュのみ、ネットワークリクエストなし)
     */
    CACHE_ONLY,

    /**
     * Network only, no caching
     * (ネットワークのみ、キャッシュなし)
     */
    NETWORK_ONLY
}

/**
 * Cache entry with expiration
 */
data class CacheEntry<T>(
    val data: T,
    val timestamp: Long,
    val expirationMs: Long = DEFAULT_EXPIRATION_MS
) {
    companion object {
        const val DEFAULT_EXPIRATION_MS = 3600_000L // 1 hour

        fun <T> create(
            data: T,
            expirationMs: Long = DEFAULT_EXPIRATION_MS
        ): CacheEntry<T> {
            return CacheEntry(
                data = data,
                timestamp = System.currentTimeMillis(),
                expirationMs = expirationMs
            )
        }
    }

    /**
     * Check if cache entry is expired
     */
    fun isExpired(): Boolean {
        val currentTime = System.currentTimeMillis()
        return (currentTime - timestamp) > expirationMs
    }

    /**
     * Get remaining time until expiration
     */
    fun remainingTimeMs(): Long {
        val currentTime = System.currentTimeMillis()
        val elapsed = currentTime - timestamp
        return (expirationMs - elapsed).coerceAtLeast(0)
    }
}

/**
 * Simple in-memory cache
 */
class InMemoryCache<K, V> {
    private val cache = mutableMapOf<K, CacheEntry<V>>()

    /**
     * Put value in cache
     */
    fun put(key: K, value: V, expirationMs: Long = CacheEntry.DEFAULT_EXPIRATION_MS) {
        cache[key] = CacheEntry.create(value, expirationMs)
    }

    /**
     * Get value from cache
     */
    fun get(key: K): V? {
        val entry = cache[key] ?: return null

        // Remove expired entries
        if (entry.isExpired()) {
            cache.remove(key)
            return null
        }

        return entry.data
    }

    /**
     * Clear entire cache
     */
    fun clear() {
        cache.clear()
    }

    /**
     * Remove specific key
     */
    fun remove(key: K) {
        cache.remove(key)
    }

    /**
     * Clean up expired entries
     */
    fun cleanExpired() {
        val expiredKeys = cache.filter { (_, entry) -> entry.isExpired() }.keys
        expiredKeys.forEach { cache.remove(it) }
    }

    /**
     * Get cache size
     */
    fun size(): Int = cache.size
}
