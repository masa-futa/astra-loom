# 🌌 Astra Loom

**Astra Loom** is an interactive stargazing application that provides astronomically accurate sky data with layered exploration capabilities.

## Overview

This app is not just a constellation catalog, but an experience platform that allows users to "touch and explore the starry sky" through switchable display layers (stars, constellations, galaxies, planets).

## Architecture

This project uses a monorepo structure with Kotlin Multiplatform (KMP) for shared business logic:

```
astra-loom/
├── shared/          # KMP shared module (astronomy engine, domain logic)
├── iosApp/          # iOS application (SwiftUI)
├── androidApp/      # Android application (Jetpack Compose)
└── docs/            # Documentation
```

## Key Features

- **Astronomically Accurate**: Position calculations based on real astronomical data
- **Layer-Based Exploration**: Toggle between stars, constellations, galaxies, and planets
- **Offline-First**: All calculations performed locally without API dependencies
- **Cross-Platform**: Shared astronomy engine across iOS and Android

## Technology Stack

- **Shared Logic**: Kotlin Multiplatform
- **iOS UI**: SwiftUI (iPadOS)
- **Android UI**: Jetpack Compose
- **Astronomy Engine**: Custom implementation with validation against Stellarium
- **Data Source**: Bright Star Catalog, constellation data

## Usage Example

### Low-Level API (Astronomy Engine)

```kotlin
// Create astronomy engine
val engine = AstronomyEngine(
    config = AstronomyEngine.EngineConfig(
        applyPrecession = true,
        applyRefraction = true
    )
)

// Define observer location (Tokyo)
val observer = Observer.Tokyo

// Get current time
val now = Clock.System.now()

// Calculate star position
val siriusPosition = engine.calculateStarPosition(
    star = Star.Sirius,
    observer = observer,
    time = now
)

// Check visibility
if (siriusPosition.isVisible()) {
    println("Sirius is visible!")
    println("Altitude: ${siriusPosition.altitudeDegrees()}°")
    println("Azimuth: ${siriusPosition.azimuthDegrees()}°")
}
```

### High-Level API (Use Cases)

```kotlin
// Setup repositories and use cases
val resourceReader = ResourceReader()
val starDataSource = LocalStarDataSource(resourceReader)
val starRepository = LocalStarRepository(starDataSource)
val astronomyEngine = AstronomyEngine()

// Get visible stars use case
val getVisibleStarsUseCase = GetVisibleStarsUseCase(starRepository, astronomyEngine)

// Get all visible stars
val result = getVisibleStarsUseCase.execute(
    observer = Observer.Tokyo,
    time = Clock.System.now(),
    maxMagnitude = 4.0
)

result.onSuccess { visibleStars ->
    visibleStars.forEach { star ->
        println("${star.star.name}: Alt=${star.altitudeDegrees}°")
    }
}

// Get constellation with stars
val constellationUseCase = GetConstellationStarsUseCase(constellationRepository, astronomyEngine)
val orion = constellationUseCase.execute("Ori", Observer.Tokyo, Clock.System.now())
```

## Project Status

🚧 Under active development

### Completed Features

#### ✅ Astronomy Engine (KMP Shared Module)
- **Domain Models**: Star, Constellation, Observer, Coordinate systems
- **Julian Date**: Calendar to JD conversion, J2000 calculations
- **Sidereal Time**: GMST, LST, and hour angle calculations
- **Coordinate Transform**: Equatorial (RA/Dec) ↔ Horizontal (Alt/Az)
- **Precession**: IAU 1976 precession correction
- **Atmospheric Refraction**: Bennett's formula with atmospheric conditions
- **AstronomyEngine API**: High-level facade with configurable corrections

#### ✅ Infrastructure Layer (KMP Shared Module)
- **Repository Pattern**: StarRepository, ConstellationRepository interfaces
- **Local Data Sources**: JSON-based with ResourceReader (iOS/Android)
- **Remote Data Sources**: API-based with Ktor HttpClient
- **Cached Repositories**: Intelligent caching with multiple strategies
- **Sample Data**: 25 bright stars, 10 major constellations
- **Use Cases**: GetVisibleStarsUseCase, GetConstellationStarsUseCase, SearchStarsUseCase

#### ✅ API Client (KMP Shared Module)
- **Ktor HttpClient**: Configured with JSON, logging, timeout, retry
- **API Endpoints**: Stars, Constellations with RESTful structure
- **Error Handling**: Typed ApiException (Network, Server, Client errors)
- **Remote Repositories**: Full implementation for Star and Constellation
- **Caching Strategy**: CACHE_FIRST, NETWORK_FIRST, CACHE_ONLY, NETWORK_ONLY
- **InMemoryCache**: With expiration and automatic cleanup
- **Platform Engines**: Darwin (iOS), Android

#### 🧪 Testing
- Comprehensive unit tests for all astronomy modules
- Integration tests for AstronomyEngine
- Validation against known astronomical values

### In Progress
- iOS UI implementation (SwiftUI)
- Integration tests for data layer

### Upcoming
- Android UI implementation (Jetpack Compose)
- Constellation rendering with lines
- Layer switching system
- Interactive star selection
- API server implementation

## Documentation

### Technical Documentation
- [Architecture](docs/architecture.md) - System architecture and module structure
- [Setup Guide](docs/setup.md) - Development environment setup

### Design Documentation
- [Requirements (RFP)](docs/requirements.md) - Project requirements and concept
- [System Design](docs/design.md) - Detailed system design (KMP + SwiftUI)
- [Astronomy Engine Design](docs/astronomy-engine-design.md) - Mathematical formulas and calculations
- [Astronomy Engine Implementation](docs/astronomy-engine-implementation.md) - Implementation and validation strategy
- [UI Specification](docs/ui-specification.md) - Screen definitions and UX design

## License

TBD
