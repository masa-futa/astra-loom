import SwiftUI

struct FOVControlView: View {
    @ObservedObject var viewportState: ViewportState
    let onFOVChange: () -> Void

    @State private var tempFOV: Double

    init(viewportState: ViewportState, onFOVChange: @escaping () -> Void) {
        self.viewportState = viewportState
        self.onFOVChange = onFOVChange
        _tempFOV = State(initialValue: viewportState.fieldOfView)
    }

    var body: some View {
        VStack(spacing: 16) {
            // Title
            HStack {
                Image(systemName: "viewfinder")
                Text("視野角")
                    .font(.headline)
                Spacer()
                Text("\(Int(tempFOV))°")
                    .font(.title3)
                    .fontWeight(.semibold)
            }

            // Slider
            Slider(value: $tempFOV, in: 30...180, step: 10)
                .onChange(of: tempFOV) { _, newValue in
                    viewportState.setFieldOfView(newValue)
                    onFOVChange()
                }

            // Quick presets
            HStack(spacing: 12) {
                PresetButton(title: "広角", fov: 150, currentFOV: $tempFOV) {
                    applyPreset(150)
                }
                PresetButton(title: "標準", fov: 120, currentFOV: $tempFOV) {
                    applyPreset(120)
                }
                PresetButton(title: "望遠", fov: 60, currentFOV: $tempFOV) {
                    applyPreset(60)
                }
            }
        }
        .padding()
        .background(.ultraThinMaterial)
        .cornerRadius(16)
    }

    private func applyPreset(_ fov: Double) {
        tempFOV = fov
        viewportState.setFieldOfView(fov)
        onFOVChange()
    }
}

private struct PresetButton: View {
    let title: String
    let fov: Double
    @Binding var currentFOV: Double
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            VStack(spacing: 4) {
                Text(title)
                    .font(.caption)
                Text("\(Int(fov))°")
                    .font(.caption2)
                    .opacity(0.8)
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, 8)
            .background(isSelected ? Color.blue.opacity(0.3) : Color.white.opacity(0.1))
            .cornerRadius(8)
        }
        .foregroundColor(.white)
    }

    private var isSelected: Bool {
        abs(currentFOV - fov) < 5
    }
}

#Preview {
    FOVControlView(viewportState: ViewportState()) {
        print("FOV changed")
    }
    .preferredColorScheme(.dark)
}
