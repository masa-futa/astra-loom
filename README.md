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

## Project Status

🚧 Under active development

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
