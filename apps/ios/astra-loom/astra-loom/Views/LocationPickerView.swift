import SwiftUI

/// 観測地点選択ビュー
struct LocationPickerView: View {
    let currentLocation: LocationPreset
    let onSelect: (LocationPreset) -> Void
    @Environment(\.dismiss) var dismiss

    var body: some View {
        NavigationView {
            List(LocationPreset.presets) { location in
                Button {
                    onSelect(location)
                    dismiss()
                } label: {
                    HStack {
                        VStack(alignment: .leading, spacing: 4) {
                            Text(location.nameJa)
                                .font(.headline)

                            Text(location.name)
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }

                        Spacer()

                        if location.id == currentLocation.id {
                            Image(systemName: "checkmark.circle.fill")
                                .foregroundColor(.blue)
                        }
                    }
                    .contentShape(Rectangle())
                }
                .buttonStyle(.plain)
            }
            .navigationTitle("観測地点")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("閉じる") {
                        dismiss()
                    }
                }
            }
        }
    }
}

#Preview {
    LocationPickerView(
        currentLocation: .tokyo,
        onSelect: { _ in }
    )
}
