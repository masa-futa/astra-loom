import SwiftUI

/// 時刻変更ビュー
struct TimeControlView: View {
    @Binding var currentTime: Date
    @Binding var useCurrentTime: Bool
    let timezone: Foundation.TimeZone
    let onTimeChange: (Date) -> Void

    @State private var selectedHour: Double = 12.0

    var body: some View {
        VStack(spacing: 16) {
            // 現在時刻モード切替
            HStack {
                Button {
                    useCurrentTime = true
                    currentTime = Date()
                    onTimeChange(currentTime)
                } label: {
                    HStack {
                        Image(systemName: useCurrentTime ? "checkmark.circle.fill" : "circle")
                        Text("現在時刻")
                    }
                }
                .buttonStyle(.bordered)
                .tint(useCurrentTime ? .blue : .gray)

                Spacer()

                if useCurrentTime {
                    // 夜に切り替えボタン
                    Button {
                        selectedHour = 21.0
                        useCurrentTime = false
                        updateTime()
                    } label: {
                        HStack(spacing: 4) {
                            Image(systemName: "moon.stars.fill")
                            Text("夜に切り替え")
                        }
                        .font(.caption)
                    }
                    .buttonStyle(.bordered)
                    .tint(.indigo)
                } else {
                    Text(formattedTime)
                        .font(.headline)
                        .monospacedDigit()
                }
            }

            if !useCurrentTime {
                // 時刻スライダー
                VStack(spacing: 8) {
                    HStack {
                        Image(systemName: "sun.horizon")
                            .foregroundColor(.orange)
                        Slider(value: $selectedHour, in: 0...24, step: 0.25) {
                            Text("時刻")
                        } onEditingChanged: { editing in
                            if !editing {
                                updateTime()
                            }
                        }
                        Image(systemName: "moon.stars")
                            .foregroundColor(.indigo)
                    }

                    // 時刻マーカー
                    HStack {
                        ForEach([0, 6, 12, 18, 24], id: \.self) { hour in
                            if hour == 0 {
                                Text("\(hour):00")
                                    .font(.caption2)
                                    .foregroundColor(.secondary)
                            } else {
                                Spacer()
                                Text("\(hour):00")
                                    .font(.caption2)
                                    .foregroundColor(.secondary)
                            }
                        }
                    }
                }

                // クイックタイムボタン
                HStack(spacing: 8) {
                    TimeQuickButton(time: "朝", hour: 6, selectedHour: $selectedHour, onSelect: updateTime)
                    TimeQuickButton(time: "昼", hour: 12, selectedHour: $selectedHour, onSelect: updateTime)
                    TimeQuickButton(time: "夕", hour: 18, selectedHour: $selectedHour, onSelect: updateTime)
                    TimeQuickButton(time: "夜", hour: 21, selectedHour: $selectedHour, onSelect: updateTime)
                    TimeQuickButton(time: "深夜", hour: 0, selectedHour: $selectedHour, onSelect: updateTime)
                }
                .font(.caption)
            }
        }
        .padding()
        .background(.ultraThinMaterial)
        .cornerRadius(16)
        .onAppear {
            if !useCurrentTime {
                var calendar = Calendar.current
                calendar.timeZone = timezone
                let components = calendar.dateComponents(in: timezone, from: currentTime)
                let hour = components.hour ?? 12
                let minute = components.minute ?? 0
                selectedHour = Double(hour) + Double(minute) / 60.0
            }
        }
    }

    private var formattedTime: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm"
        return formatter.string(from: currentTime)
    }

    private func updateTime() {
        var calendar = Calendar.current
        calendar.timeZone = timezone

        let hour = Int(selectedHour)
        let minute = Int((selectedHour - Double(hour)) * 60)

        // 現在の日付を指定されたタイムゾーンで取得
        var components = calendar.dateComponents(in: timezone, from: Date())
        components.hour = hour
        components.minute = minute
        components.second = 0

        if let newTime = calendar.date(from: components) {
            currentTime = newTime
            onTimeChange(newTime)
        }
    }
}

/// クイックタイム選択ボタン
struct TimeQuickButton: View {
    let time: String
    let hour: Int
    @Binding var selectedHour: Double
    let onSelect: () -> Void

    var body: some View {
        Button {
            selectedHour = Double(hour)
            onSelect()
        } label: {
            Text(time)
                .padding(.horizontal, 12)
                .padding(.vertical, 6)
                .background(
                    Int(selectedHour) == hour ? Color.blue : Color.gray.opacity(0.2)
                )
                .foregroundColor(
                    Int(selectedHour) == hour ? .white : .primary
                )
                .cornerRadius(8)
        }
    }
}

#Preview {
    TimeControlView(
        currentTime: .constant(Date()),
        useCurrentTime: .constant(false),
        timezone: Foundation.TimeZone.current,
        onTimeChange: { _ in }
    )
    .padding()
}
