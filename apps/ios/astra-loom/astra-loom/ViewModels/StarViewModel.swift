import SwiftUI
import shared

/// View model for rendering a single star
struct StarViewModel: Identifiable {
    let id: String
    let name: String
    let x: CGFloat
    let y: CGFloat
    let size: CGFloat
    let brightness: Double
    let color: Color
    let magnitude: Double
    let altitude: Double
    let azimuth: Double
    let spectralType: String?
    let visibleStar: VisibleStar

    init(visibleStar: VisibleStar, screenSize: CGSize, viewportState: ViewportState) {
        self.visibleStar = visibleStar
        self.id = visibleStar.star.id
        self.name = visibleStar.star.name ?? "Unknown"
        self.magnitude = visibleStar.star.magnitude
        self.altitude = visibleStar.altitudeDegrees
        self.azimuth = visibleStar.azimuthDegrees
        self.spectralType = visibleStar.star.spectralType

        // Calculate screen position using stereographic projection
        let position = Self.calculateScreenPosition(
            starAltitude: visibleStar.altitudeDegrees,
            starAzimuth: visibleStar.azimuthDegrees,
            centerAltitude: viewportState.centerAltitude,
            centerAzimuth: viewportState.centerAzimuth,
            fieldOfView: viewportState.fieldOfView,
            screenSize: screenSize
        )

        self.x = position.x
        self.y = position.y

        // Calculate size based on magnitude
        // Brighter stars (lower magnitude) are larger
        let baseSize = 8.0
        self.size = CGFloat(baseSize * pow(2.0, (1.5 - visibleStar.star.magnitude) / 2.0))

        // Brightness for glow effect
        self.brightness = max(0.0, min(1.0, (4.0 - visibleStar.star.magnitude) / 5.0))

        // Color based on spectral type
        self.color = Self.colorForSpectralType(visibleStar.star.spectralType ?? "")
    }

    /// Calculate screen position using stereographic projection with spherical law of cosines
    static func calculateScreenPosition(
        starAltitude: Double,
        starAzimuth: Double,
        centerAltitude: Double,
        centerAzimuth: Double,
        fieldOfView: Double,
        screenSize: CGSize
    ) -> CGPoint {
        // Convert to radians
        let starAltRad = starAltitude * .pi / 180.0
        let starAzRad = starAzimuth * .pi / 180.0
        let centerAltRad = centerAltitude * .pi / 180.0
        let centerAzRad = centerAzimuth * .pi / 180.0

        // Calculate angular distance using spherical law of cosines
        let cosAngularDistance = sin(starAltRad) * sin(centerAltRad) +
                                 cos(starAltRad) * cos(centerAltRad) * cos(starAzRad - centerAzRad)
        let angularDistance = acos(max(-1.0, min(1.0, cosAngularDistance)))

        // Calculate bearing from center to star
        let y = sin(starAzRad - centerAzRad) * cos(starAltRad)
        let x = cos(centerAltRad) * sin(starAltRad) -
                sin(centerAltRad) * cos(starAltRad) * cos(starAzRad - centerAzRad)
        let bearing = atan2(y, x)

        // Project to screen using FOV scaling
        let screenRadius = min(screenSize.width, screenSize.height) / 2.0
        let fovRad = fieldOfView * .pi / 180.0
        let radius = angularDistance / fovRad * screenRadius

        // Calculate screen position
        let screenX = screenSize.width / 2.0 + radius * sin(bearing)
        let screenY = screenSize.height / 2.0 - radius * cos(bearing)

        return CGPoint(x: screenX, y: screenY)
    }

    /// Convert spectral type to color
    static func colorForSpectralType(_ spectralType: String) -> Color {
        guard let firstChar = spectralType.first else {
            return Color(red: 1.0, green: 1.0, blue: 1.0) // Default white
        }

        switch firstChar {
        case "O", "B": // Blue-white
            return Color(red: 155/255, green: 176/255, blue: 255/255)
        case "A": // White
            return Color(red: 202/255, green: 215/255, blue: 255/255)
        case "F": // Yellow-white
            return Color(red: 248/255, green: 247/255, blue: 255/255)
        case "G": // Yellow
            return Color(red: 255/255, green: 244/255, blue: 234/255)
        case "K": // Orange
            return Color(red: 255/255, green: 210/255, blue: 161/255)
        case "M": // Red
            return Color(red: 255/255, green: 204/255, blue: 111/255)
        default:
            return Color(red: 1.0, green: 1.0, blue: 1.0)
        }
    }
}
