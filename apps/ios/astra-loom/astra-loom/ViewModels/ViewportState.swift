import SwiftUI
import Combine

/// Manages the viewport configuration for the sky view
@MainActor
final class ViewportState: ObservableObject {
    /// Center altitude in degrees (-5° to 95°)
    @Published var centerAltitude: Double = 45.0

    /// Center azimuth in degrees (0° to 360°)
    @Published var centerAzimuth: Double = 180.0

    /// Field of view in degrees (30° to 180°)
    @Published var fieldOfView: Double = 90.0

    /// Update altitude with clamping
    func updateAltitude(_ delta: Double) {
        centerAltitude = min(95.0, max(-5.0, centerAltitude + delta))
    }

    /// Update azimuth with wrapping
    func updateAzimuth(_ delta: Double) {
        centerAzimuth = (centerAzimuth + delta).truncatingRemainder(dividingBy: 360.0)
        if centerAzimuth < 0 {
            centerAzimuth += 360.0
        }
    }

    /// Set field of view with clamping
    func setFieldOfView(_ fov: Double) {
        fieldOfView = min(180.0, max(30.0, fov))
    }

    /// Get primary cardinal direction based on azimuth
    var primaryDirection: String {
        let directions = ["N", "NE", "E", "SE", "S", "SW", "W", "NW"]
        let index = Int((centerAzimuth + 22.5) / 45.0) % 8
        return directions[index]
    }
}
