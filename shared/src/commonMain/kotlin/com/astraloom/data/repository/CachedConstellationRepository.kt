package com.astraloom.data.repository

import com.astraloom.data.cache.CacheStrategy
import com.astraloom.data.cache.InMemoryCache
import com.astraloom.domain.Constellation
import com.astraloom.domain.Star
import com.astraloom.repository.ConstellationRepository

/**
 * Cached implementation of ConstellationRepository
 * (キャッシュ付きConstellationRepository実装)
 */
class CachedConstellationRepository(
    private val localRepository: ConstellationRepository,
    private val remoteRepository: ConstellationRepository,
    private val cacheStrategy: CacheStrategy = CacheStrategy.CACHE_FIRST,
    private val cacheExpirationMs: Long = 3600_000L // 1 hour
) : ConstellationRepository {

    private val cache = InMemoryCache<String, List<Constellation>>()
    private val singleConstellationCache = InMemoryCache<String, Constellation>()

    companion object {
        private const val ALL_CONSTELLATIONS_KEY = "all_constellations"
        private const val MAJOR_CONSTELLATIONS_KEY = "major_constellations"
    }

    override suspend fun getAllConstellations(): Result<List<Constellation>> {
        return executeWithCache(
            cacheKey = ALL_CONSTELLATIONS_KEY,
            cacheGetter = { cache.get(ALL_CONSTELLATIONS_KEY) },
            cacheSetter = { cache.put(ALL_CONSTELLATIONS_KEY, it, cacheExpirationMs) },
            localFetcher = { localRepository.getAllConstellations() },
            remoteFetcher = { remoteRepository.getAllConstellations() }
        )
    }

    override suspend fun getConstellationById(id: String): Result<Constellation?> {
        return executeWithCache(
            cacheKey = id,
            cacheGetter = { singleConstellationCache.get(id) },
            cacheSetter = { it?.let { c -> singleConstellationCache.put(id, c, cacheExpirationMs) } },
            localFetcher = { localRepository.getConstellationById(id) },
            remoteFetcher = { remoteRepository.getConstellationById(id) }
        )
    }

    override suspend fun getConstellationByName(name: String): Result<Constellation?> {
        return when (cacheStrategy) {
            CacheStrategy.CACHE_ONLY, CacheStrategy.CACHE_FIRST ->
                localRepository.getConstellationByName(name)
            CacheStrategy.NETWORK_ONLY, CacheStrategy.NETWORK_FIRST ->
                remoteRepository.getConstellationByName(name)
        }
    }

    override suspend fun getStarsInConstellation(constellationId: String): Result<List<Star>> {
        return when (cacheStrategy) {
            CacheStrategy.CACHE_ONLY, CacheStrategy.CACHE_FIRST ->
                localRepository.getStarsInConstellation(constellationId)
            CacheStrategy.NETWORK_ONLY, CacheStrategy.NETWORK_FIRST ->
                remoteRepository.getStarsInConstellation(constellationId)
        }
    }

    override suspend fun getConstellationLines(constellationId: String): Result<List<Pair<String, String>>> {
        return when (cacheStrategy) {
            CacheStrategy.CACHE_ONLY, CacheStrategy.CACHE_FIRST ->
                localRepository.getConstellationLines(constellationId)
            CacheStrategy.NETWORK_ONLY, CacheStrategy.NETWORK_FIRST ->
                remoteRepository.getConstellationLines(constellationId)
        }
    }

    override suspend fun searchConstellationsByName(query: String): Result<List<Constellation>> {
        return when (cacheStrategy) {
            CacheStrategy.CACHE_ONLY, CacheStrategy.CACHE_FIRST ->
                localRepository.searchConstellationsByName(query)
            CacheStrategy.NETWORK_ONLY, CacheStrategy.NETWORK_FIRST ->
                remoteRepository.searchConstellationsByName(query)
        }
    }

    override suspend fun getMajorConstellations(): Result<List<Constellation>> {
        return executeWithCache(
            cacheKey = MAJOR_CONSTELLATIONS_KEY,
            cacheGetter = { cache.get(MAJOR_CONSTELLATIONS_KEY) },
            cacheSetter = { cache.put(MAJOR_CONSTELLATIONS_KEY, it, cacheExpirationMs) },
            localFetcher = { localRepository.getMajorConstellations() },
            remoteFetcher = { remoteRepository.getMajorConstellations() }
        )
    }

    override suspend fun refresh(): Result<Unit> {
        cache.clear()
        singleConstellationCache.clear()
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
                cacheGetter()?.let { return Result.success(it) }
                localFetcher().getOrNull()?.let {
                    cacheSetter(it)
                    return Result.success(it)
                }
                remoteFetcher().onSuccess { cacheSetter(it) }
            }

            CacheStrategy.NETWORK_FIRST -> {
                val remoteResult = remoteFetcher()
                if (remoteResult.isSuccess) {
                    remoteResult.getOrNull()?.let { cacheSetter(it) }
                    return remoteResult
                }
                cacheGetter()?.let { return Result.success(it) }
                localFetcher().onSuccess { cacheSetter(it) }
            }

            CacheStrategy.CACHE_ONLY -> {
                cacheGetter()?.let { return Result.success(it) }
                localFetcher().onSuccess { cacheSetter(it) }
            }

            CacheStrategy.NETWORK_ONLY -> {
                remoteFetcher()
            }
        }
    }
}
