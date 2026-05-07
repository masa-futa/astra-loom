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

    init(visibleStar: VisibleStar, screenSize: CGSize, viewportSize: CGFloat = 120.0) {
        self.visibleStar = visibleStar
        self.id = visibleStar.star.id
        self.name = visibleStar.star.name ?? "Unknown"
        self.magnitude = visibleStar.star.magnitude
        self.altitude = visibleStar.altitudeDegrees
        self.azimuth = visibleStar.azimuthDegrees
        self.spectralType = visibleStar.star.spectralType

        // Convert azimuth/altitude to screen coordinates
        // Azimuth: 0° = North, 90° = East, 180° = South, 270° = West
        // Map to screen: center is zenith (altitude 90°)

        let azimuthRad = visibleStar.azimuthDegrees * .pi / 180.0
        let altitude = visibleStar.altitudeDegrees

        // Distance from center based on altitude (90° = center, 0° = edge)
        let radius = (90.0 - altitude) / viewportSize * min(screenSize.width, screenSize.height) / 2.0

        // Calculate screen position
        self.x = screenSize.width / 2 + CGFloat(radius * sin(azimuthRad))
        self.y = screenSize.height / 2 - CGFloat(radius * cos(azimuthRad))

        // Calculate size based on magnitude
        // Brighter stars (lower magnitude) are larger
        let baseSize = 8.0
        self.size = CGFloat(baseSize * pow(2.0, (1.5 - visibleStar.star.magnitude) / 2.0))

        // Brightness for glow effect
        self.brightness = max(0.0, min(1.0, (4.0 - visibleStar.star.magnitude) / 5.0))

        // Color based on spectral type
        self.color = Self.colorForSpectralType(visibleStar.star.spectralType ?? "")
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
