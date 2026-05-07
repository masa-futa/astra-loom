import SwiftUI

struct CompassView: View {
    @ObservedObject var viewportState: ViewportState

    var body: some View {
        VStack(spacing: 8) {
            // Cardinal directions
            HStack(spacing: 20) {
                DirectionLabel("N", isActive: isNorth)
                DirectionLabel("E", isActive: isEast)
                DirectionLabel("S", isActive: isSouth)
                DirectionLabel("W", isActive: isWest)
            }
            .font(.caption2)

            // Direction arrow + azimuth + altitude
            HStack(spacing: 12) {
                Image(systemName: "location.north.fill")
                    .rotationEffect(.degrees(-viewportState.centerAzimuth))
                    .font(.caption)

                Text("\(Int(viewportState.centerAzimuth))° \(viewportState.primaryDirection)")
                    .font(.caption)
                    .fontWeight(.medium)

                Text("仰角 \(Int(viewportState.centerAltitude))°")
                    .font(.caption)
                    .opacity(0.8)
            }
        }
        .foregroundColor(.white)
        .padding(.horizontal, 16)
        .padding(.vertical, 10)
        .background(.ultraThinMaterial.opacity(0.5))
        .cornerRadius(12)
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
