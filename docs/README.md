# Astra Loom Documentation

## Overview

This directory contains comprehensive documentation for the Astra Loom project, covering requirements, design, architecture, and implementation details.

## Document Index

### 📋 Project Planning

- **[requirements.md](requirements.md)** - RFP (Request for Proposal)
  - Project overview and concept
  - Feature requirements
  - Data requirements
  - Stellarium integration strategy
  - MVP definition and roadmap

### 🏗️ System Design

- **[architecture.md](architecture.md)** - System Architecture
  - High-level architecture diagram
  - Module structure
  - Technology stack
  - Design principles

- **[design.md](design.md)** - Detailed System Design
  - KMP module design
  - iOS (SwiftUI) integration
  - Android (Compose) integration
  - State management
  - Data strategy

### 🔭 Astronomy Engine

- **[astronomy-engine-design.md](astronomy-engine-design.md)** - Mathematical Design
  - Calculation flow
  - Mathematical formulas (Julian Date, Sidereal Time, etc.)
  - Coordinate transformations
  - Precession corrections
  - Atmospheric refraction

- **[astronomy-engine-implementation.md](astronomy-engine-implementation.md)** - Implementation Guide
  - Module structure
  - Core implementations with code examples
  - Performance optimization
  - Validation strategy (Stellarium comparison)
  - Testing approach

### 🎨 User Interface

- **[ui-specification.md](ui-specification.md)** - UI/UX Specification
  - Screen definitions
  - UI components
  - Interaction patterns
  - Display modes (Minimal, Learning, Exploration)
  - State management
  - Design principles

### 🛠️ Development

- **[setup.md](setup.md)** - Development Setup Guide
  - Prerequisites
  - Build instructions
  - Development workflow
  - Troubleshooting

## Reading Order

### For Understanding the Project
1. requirements.md - Understand the vision and goals
2. ui-specification.md - See what users will experience
3. architecture.md - Understand the technical approach

### For Implementation
1. design.md - Understand the system structure
2. astronomy-engine-design.md - Learn the mathematical foundation
3. astronomy-engine-implementation.md - See how to implement
4. setup.md - Set up your environment

## Key Concepts

### Layered Exploration
The app uses a layer-based approach where users can toggle between:
- Stars (恒星レイヤー)
- Constellations (星座レイヤー)
- Galaxies (銀河・深宇宙レイヤー)
- Planets (惑星レイヤー)

### Astronomical Accuracy
All calculations are performed locally using:
- Julian Date calculations
- Sidereal time
- Coordinate transformations (RA/Dec → Alt/Az)
- Precession corrections
- Atmospheric refraction adjustments

### Technology Stack
- **Shared Logic**: Kotlin Multiplatform (KMP)
- **iOS UI**: SwiftUI
- **Android UI**: Jetpack Compose
- **Validation**: Against Stellarium data

## Contributing

When adding new documentation:
1. Use clear, descriptive filenames
2. Update this index
3. Add links in the main README.md
4. Follow the existing markdown format
