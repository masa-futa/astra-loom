# Setup Guide

## Prerequisites

### For Shared Module (KMP)

- JDK 17 or higher
- Gradle 8.0 or higher
- Kotlin 1.9.23 or higher

### For iOS Development

- macOS (for iOS development)
- Xcode 15 or higher
- CocoaPods (for dependency management)

### For Android Development

- Android Studio Hedgehog or higher
- Android SDK 34
- Minimum SDK 24

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/astra-loom.git
cd astra-loom
```

### 2. Build Shared Module

```bash
./gradlew :shared:build
```

### 3. iOS Setup

```bash
cd iosApp
# Additional setup steps will be added when iOS project is created
```

### 4. Android Setup

```bash
# Open the project in Android Studio
# Or build from command line:
./gradlew :androidApp:build
```

## Development Workflow

### Running Tests

```bash
# Run all tests
./gradlew test

# Run shared module tests only
./gradlew :shared:test
```

### Building iOS Framework

```bash
./gradlew :shared:embedAndSignAppleFrameworkForXcode
```

### Code Style

This project follows the official Kotlin code style.

## Troubleshooting

### Gradle Build Issues

- Ensure you have JDK 17 installed
- Try `./gradlew clean` before building

### iOS Framework Issues

- Run `pod install` in iosApp directory
- Clean Xcode build folder (Cmd+Shift+K)

## Documentation

- [Architecture](architecture.md)
- Design documents in `/Users/masakifutami/Desktop/`
