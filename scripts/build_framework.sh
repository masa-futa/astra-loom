#!/bin/bash

set -e

# Navigate to project root
cd "$SRCROOT/../../.."

# Build the KMP framework
./gradlew :shared:buildXcodeFramework

echo "✅ KMP Framework build completed"
