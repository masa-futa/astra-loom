package com.astraloom.di

import com.astraloom.api.KtorClient
import com.astraloom.api.repository.RemoteConstellationRepository
import com.astraloom.api.repository.RemoteStarRepository
import com.astraloom.api.source.RemoteConstellationDataSource
import com.astraloom.api.source.RemoteStarDataSource
import com.astraloom.astronomy.AstronomyEngine
import com.astraloom.data.cache.CacheStrategy
import com.astraloom.data.repository.CachedConstellationRepository
import com.astraloom.data.repository.CachedStarRepository
import com.astraloom.data.repository.LocalConstellationRepository
import com.astraloom.data.repository.LocalStarRepository
import com.astraloom.data.source.LocalConstellationDataSource
import com.astraloom.data.source.LocalStarDataSource
import com.astraloom.data.source.ResourceReader
import com.astraloom.manager.*
import com.astraloom.repository.ConstellationRepository
import com.astraloom.repository.StarRepository
import com.astraloom.usecase.GetConstellationStarsUseCase
import com.astraloom.usecase.GetVisibleStarsUseCase
import com.astraloom.usecase.SearchStarsUseCase

/**
 * Factory for creating AstraLoomManager with all dependencies
 * (AstraLoomManagerとその依存関係を生成するファクトリ)
 *
 * This factory handles the complex dependency injection setup,
 * allowing platform-specific code to easily create a configured manager.
 *
 * Usage:
 * ```kotlin
 * val manager = ManagerFactory.create(
 *     config = AstraLoomConfig(
 *         cacheStrategy = CacheStrategy.CACHE_FIRST
 *     )
 * )
 * ```
 */
object ManagerFactory {

    /**
     * Create AstraLoomManager with specified configuration
     *
     * @param config Configuration for the manager
     * @return Fully configured AstraLoomManager
     */
    fun create(config: AstraLoomConfig): AstraLoomManager {
        // Create repositories based on configuration
        val starRepository = createStarRepository(config)
        val constellationRepository = createConstellationRepository(config, starRepository)

        // Create astronomy engine
        val astronomyEngine = createAstronomyEngine(config)

        // Create use cases
        val getVisibleStarsUseCase = GetVisibleStarsUseCase(starRepository, astronomyEngine)
        val searchStarsUseCase = SearchStarsUseCase(starRepository, astronomyEngine)
        val getConstellationStarsUseCase = GetConstellationStarsUseCase(
            constellationRepository,
            astronomyEngine
        )

        // Create managers
        val starManager = StarManager(getVisibleStarsUseCase, searchStarsUseCase)
        val constellationManager = ConstellationManager(getConstellationStarsUseCase)
        val astronomyManager = AstronomyManager(astronomyEngine)

        // Create top-level manager
        return AstraLoomManager(starManager, constellationManager, astronomyManager)
    }

    /**
     * Create StarRepository based on configuration
     */
    private fun createStarRepository(config: AstraLoomConfig): StarRepository {
        val resourceReader = ResourceReader()

        // Local repository
        val localDataSource = LocalStarDataSource(resourceReader)
        val localRepository = LocalStarRepository(localDataSource)

        // If API URL is not configured, use local only
        if (config.apiBaseUrl == null) {
            return localRepository
        }

        // Remote repository (if API URL is configured)
        val httpClient = KtorClient.create(
            com.astraloom.api.ClientConfig(baseUrl = config.apiBaseUrl)
        )
        val remoteDataSource = RemoteStarDataSource(httpClient)
        val remoteRepository = RemoteStarRepository(remoteDataSource)

        // Cached repository (combines local and remote)
        return CachedStarRepository(
            localRepository = localRepository,
            remoteRepository = remoteRepository,
            cacheStrategy = config.cacheStrategy,
            cacheExpirationMs = config.cacheExpirationMs
        )
    }

    /**
     * Create ConstellationRepository based on configuration
     */
    private fun createConstellationRepository(
        config: AstraLoomConfig,
        starRepository: StarRepository
    ): ConstellationRepository {
        val resourceReader = ResourceReader()

        // Local repository
        val localDataSource = LocalConstellationDataSource(resourceReader)
        val localRepository = LocalConstellationRepository(localDataSource, starRepository)

        // If API URL is not configured, use local only
        if (config.apiBaseUrl == null) {
            return localRepository
        }

        // Remote repository (if API URL is configured)
        val httpClient = KtorClient.create(
            com.astraloom.api.ClientConfig(baseUrl = config.apiBaseUrl)
        )
        val remoteDataSource = RemoteConstellationDataSource(httpClient)
        val remoteRepository = RemoteConstellationRepository(remoteDataSource, starRepository)

        // Cached repository (combines local and remote)
        return CachedConstellationRepository(
            localRepository = localRepository,
            remoteRepository = remoteRepository,
            cacheStrategy = config.cacheStrategy,
            cacheExpirationMs = config.cacheExpirationMs
        )
    }

    /**
     * Create AstronomyEngine with specified configuration
     */
    private fun createAstronomyEngine(config: AstraLoomConfig): AstronomyEngine {
        return AstronomyEngine(
            AstronomyEngine.EngineConfig(
                applyPrecession = config.applyPrecession,
                applyRefraction = config.applyRefraction
            )
        )
    }

    /**
     * Create AstraLoomManager with default configuration
     * Uses local data only, cache-first strategy
     */
    fun createDefault(): AstraLoomManager {
        return create(AstraLoomConfig())
    }

    /**
     * Create AstraLoomManager for offline use
     * Uses local data only, cache-only strategy
     */
    fun createOffline(): AstraLoomManager {
        return create(
            AstraLoomConfig(
                cacheStrategy = CacheStrategy.CACHE_ONLY
            )
        )
    }

    /**
     * Create AstraLoomManager with API integration
     */
    fun createWithApi(
        apiBaseUrl: String,
        cacheStrategy: CacheStrategy = CacheStrategy.NETWORK_FIRST
    ): AstraLoomManager {
        return create(
            AstraLoomConfig(
                apiBaseUrl = apiBaseUrl,
                cacheStrategy = cacheStrategy
            )
        )
    }
}
