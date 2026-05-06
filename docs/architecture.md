# Architecture

## Overview

Astra Loom follows a clean architecture approach with clear separation of concerns:

```
┌─────────────────────────────────────────┐
│           Presentation Layer            │
│  ┌─────────────┐      ┌──────────────┐ │
│  │   SwiftUI   │      │Jetpack Compose│ │
│  │   (iOS)     │      │  (Android)    │ │
│  └─────────────┘      └──────────────┘ │
└─────────────────────────────────────────┘
                   ↓
┌─────────────────────────────────────────┐
│      Shared Business Logic (KMP)        │
│  ┌──────────────────────────────────┐  │
│  │         Use Cases                │  │
│  └──────────────────────────────────┘  │
│  ┌──────────────────────────────────┐  │
│  │      Domain Models               │  │
│  └──────────────────────────────────┘  │
│  ┌──────────────────────────────────┐  │
│  │    Astronomy Engine              │  │
│  │  - Coordinate Transform          │  │
│  │  - Julian Date Calculation       │  │
│  │  - Sidereal Time                 │  │
│  │  - Precession                    │  │
│  └──────────────────────────────────┘  │
│  ┌──────────────────────────────────┐  │
│  │       Repository                 │  │
│  └──────────────────────────────────┘  │
└─────────────────────────────────────────┘
                   ↓
┌─────────────────────────────────────────┐
│           Data Layer                    │
│         (Local JSON Data)               │
└─────────────────────────────────────────┘
```

## Module Structure

### shared/

The KMP shared module contains:

- **astronomy/**: Core astronomical calculation engine
  - Pure functions for coordinate transformations
  - Julian date calculations
  - Sidereal time calculations
  - Precession corrections

- **domain/**: Domain models and business entities
  - Star, Constellation, Planet models
  - Observer (location) model
  - Coordinate models (RA/Dec, Alt/Az)

- **usecase/**: Application-specific business logic
  - GetVisibleStarsUseCase
  - GetConstellationsUseCase
  - CalculateStarPositionUseCase

- **repository/**: Data access abstraction
  - StarRepository
  - ConstellationRepository

### iosApp/

SwiftUI-based iOS application

- Views (SwiftUI)
- ViewModels
- Integration with shared KMP framework

### androidApp/

Jetpack Compose-based Android application

- UI (Compose)
- ViewModels
- Integration with shared KMP module

## Key Design Principles

1. **Platform Independence**: All astronomy calculations are pure Kotlin
2. **Testability**: Pure functions enable comprehensive unit testing
3. **Performance**: Calculations are optimized and cached where appropriate
4. **Accuracy**: Validated against Stellarium for astronomical correctness
5. **Offline-First**: No external API dependencies for core functionality
