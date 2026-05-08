import SwiftUI

struct CompassView: View {
    @ObservedObject var viewportState: ViewportState

    var body: some View {
        VStack(spacing: 8) {
            // Main direction display
            HStack(spacing: 12) {
                Image(systemName: "location.north.fill")
                    .rotationEffect(.degrees(-viewportState.centerAzimuth))
                    .font(.title3)
                    .foregroundColor(.blue)

                VStack(alignment: .leading, spacing: 2) {
                    HStack(spacing: 6) {
                        Text(directionText)
                            .font(.title3)
                            .fontWeight(.bold)
                            .foregroundColor(directionColor)

                        Text("を向いています")
                            .font(.caption)
                            .opacity(0.9)
                    }

                    HStack(spacing: 12) {
                        Text("\(Int(viewportState.centerAzimuth))°")
                            .font(.caption2)
                            .opacity(0.7)

                        Text("仰角 \(Int(viewportState.centerAltitude))°")
                            .font(.caption2)
                            .opacity(0.7)
                    }
                }
            }

            // Cardinal directions
            HStack(spacing: 20) {
                DirectionLabel("N", isActive: isNorth)
                DirectionLabel("E", isActive: isEast)
                DirectionLabel("S", isActive: isSouth)
                DirectionLabel("W", isActive: isWest)
            }
            .font(.caption2)
        }
        .foregroundColor(.white)
        .padding(.horizontal, 16)
        .padding(.vertical, 10)
        .background(.ultraThinMaterial.opacity(0.6))
        .cornerRadius(12)
    }

    private var directionText: String {
        switch viewportState.primaryDirection {
        case "N": return "北"
        case "NE": return "北東"
        case "E": return "東"
        case "SE": return "南東"
        case "S": return "南"
        case "SW": return "南西"
        case "W": return "西"
        case "NW": return "北西"
        default: return viewportState.primaryDirection
        }
    }

    private var directionColor: Color {
        switch viewportState.primaryDirection {
        case "N", "NE", "NW": return .cyan
        case "S", "SE", "SW": return .orange
        case "E": return .yellow
        case "W": return .purple
        default: return .white
        }
    }

    private var isNorth: Bool {
        let az = viewportState.centerAzimuth
        return az < 45 || az >= 315
    }

    private var isEast: Bool {
        let az = viewportState.centerAzimuth
        return az >= 45 && az < 135
    }

    private var isSouth: Bool {
        let az = viewportState.centerAzimuth
        return az >= 135 && az < 225
    }

    private var isWest: Bool {
        let az = viewportState.centerAzimuth
        return az >= 225 && az < 315
    }
}

private struct DirectionLabel: View {
    let text: String
    let isActive: Bool

    init(_ text: String, isActive: Bool) {
        self.text = text
        self.isActive = isActive
    }

    var body: some View {
        Text(text)
            .fontWeight(isActive ? .bold : .regular)
            .opacity(isActive ? 1.0 : 0.5)
            .foregroundColor(isActive ? .blue : .white)
    }
}

#Preview {
    VStack(spacing: 20) {
        CompassView(viewportState: ViewportState())

        let northState = ViewportState()
        CompassView(viewportState: {
            northState.centerAzimuth = 0
            return northState
        }())

        let eastState = ViewportState()
        CompassView(viewportState: {
            eastState.centerAzimuth = 90
            return eastState
        }())
    }
    .preferredColorScheme(.dark)
    .padding()
    .background(Color.black)
}
