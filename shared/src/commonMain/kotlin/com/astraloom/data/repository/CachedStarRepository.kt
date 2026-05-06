package com.astraloom.data.repository

import com.astraloom.data.cache.CacheStrategy
import com.astraloom.data.cache.InMemoryCache
import com.astraloom.domain.Star
import com.astraloom.repository.StarRepository

/**
 * Cached implementation of StarRepository
 * Combines local and remote repositories with caching
 * (ローカルとリモートリポジトリをキャッシングで組み合わせた実装)
 */
class CachedStarRepository(
    private val localRepository: StarRepository,
    private val remoteRepository: StarRepository,
    private val cacheStrategy: CacheStrategy = CacheStrategy.CACHE_FIRST,
    private val cacheExpirationMs: Long = 3600_000L // 1 hour
) : StarRepository {

    private val cache = InMemoryCache<String, List<Star>>()
    private val singleStarCache = InMemoryCache<String, Star>()

    companion object {
        private const val ALL_STARS_KEY = "all_stars"
        private const val BRIGHT_STARS_KEY = "bright_stars"
    }

    override suspend fun getAllStars(): Result<List<Star>> {
        return executeWithCache(
            cacheKey = ALL_STARS_KEY,
            cacheGetter = { cache.get(ALL_STARS_KEY) },
            cacheSetter = { cache.put(ALL_STARS_KEY, it, cacheExpirationMs) },
            localFetcher = { localRepository.getAllStars() },
            remoteFetcher = { remoteRepository.getAllStars() }
        )
    }

    override suspend fun getStarById(id: String): Result<Star?> {
        return executeWithCache(
            cacheKey = id,
            cacheGetter = { singleStarCache.get(id) },
            cacheSetter = { it?.let { star -> singleStarCache.put(id, star, cacheExpirationMs) } },
            localFetcher = { localRepository.getStarById(id) },
            remoteFetcher = { remoteRepository.getStarById(id) }
        )
    }

    override suspend fun getStarsByMagnitude(
        maxMagnitude: Double,
        minMagnitude: Double
    ): Result<List<Star>> {
        val cacheKey = "magnitude_${maxMagnitude}_$minMagnitude"
        return executeWithCache(
            cacheKey = cacheKey,
            cacheGetter = { cache.get(cacheKey) },
            cacheSetter = { cache.put(cacheKey, it, cacheExpirationMs) },
            localFetcher = { localRepository.getStarsByMagnitude(maxMagnitude, minMagnitude) },
            remoteFetcher = { remoteRepository.getStarsByMagnitude(maxMagnitude, minMagnitude) }
        )
    }

    override suspend fun getBrightStars(): Result<List<Star>> {
        return executeWithCache(
            cacheKey = BRIGHT_STARS_KEY,
            cacheGetter = { cache.get(BRIGHT_STARS_KEY) },
            cacheSetter = { cache.put(BRIGHT_STARS_KEY, it, cacheExpirationMs) },
            localFetcher = { localRepository.getBrightStars() },
            remoteFetcher = { remoteRepository.getBrightStars() }
        )
    }

    override suspend fun getStarsByIds(ids: List<String>): Result<List<Star>> {
        val cacheKey = "ids_${ids.sorted().joinToString("_")}"
        return executeWithCache(
            cacheKey = cacheKey,
            cacheGetter = { cache.get(cacheKey) },
            cacheSetter = { cache.put(cacheKey, it, cacheExpirationMs) },
            localFetcher = { localRepository.getStarsByIds(ids) },
            remoteFetcher = { remoteRepository.getStarsByIds(ids) }
        )
    }

    override suspend fun searchStarsByName(query: String): Result<List<Star>> {
        // Search is typically not cached as results may change frequently
        return when (cacheStrategy) {
            CacheStrategy.CACHE_ONLY, CacheStrategy.CACHE_FIRST ->
                localRepository.searchStarsByName(query)
            CacheStrategy.NETWORK_ONLY, CacheStrategy.NETWORK_FIRST ->
                remoteRepository.searchStarsByName(query)
        }
    }

    override suspend fun refresh(): Result<Unit> {
        cache.clear()
        singleStarCache.clear()
        return remoteRepository.refresh()
    }

    /**
     * Execute request with caching strategy
     */
    private suspend fun <T> executeWithCache(
        cacheKey: String,
        cacheGetter: () -> T?,
        cacheSetter: (T) -> Unit,
        localFetcher: suspend () -> Result<T>,
        remoteFetcher: suspend () -> Result<T>
    ): Result<T> {
        return when (cacheStrategy) {
            CacheStrategy.CACHE_FIRST -> {
                // Try cache first
                val cachedData = cacheGetter()
                if (cachedData != null) {
                    return Result.success(cachedData)
                }

                // Cache miss, try local
                localFetcher().getOrNull()?.let {
                    cacheSetter(it)
                    return Result.success(it)
                }

                // Local miss, try remote
                remoteFetcher().onSuccess { cacheSetter(it) }
            }

            CacheStrategy.NETWORK_FIRST -> {
                // Try remote first
                val remoteResult = remoteFetcher()
                if (remoteResult.isSuccess) {
                    remoteResult.getOrNull()?.let { cacheSetter(it) }
                    return remoteResult
                }

                // Remote failed, try cache
                cacheGetter()?.let { return Result.success(it) }

                // Cache miss, try local
                localFetcher().onSuccess { cacheSetter(it) }
            }

            CacheStrategy.CACHE_ONLY -> {
                // Try cache
                cacheGetter()?.let { return Result.success(it) }

                // Cache miss, try local
                localFetcher().onSuccess { cacheSetter(it) }
            }

            CacheStrategy.NETWORK_ONLY -> {
                // Remote only, no caching
                remoteFetcher()
            }
        }
    }
}
