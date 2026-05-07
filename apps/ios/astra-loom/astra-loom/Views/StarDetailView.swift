import SwiftUI
import shared

/// 星の詳細情報ビュー
struct StarDetailView: View {
    let star: StarViewModel
    let onDismiss: () -> Void

    var body: some View {
        VStack(spacing: 0) {
            // ヘッダー
            HStack {
                Text("星の詳細")
                    .font(.headline)

                Spacer()

                Button(action: {
                    onDismiss()
                }) {
                    Image(systemName: "xmark.circle.fill")
                        .font(.title2)
                        .foregroundColor(.secondary)
                }
            }
            .padding()
            .background(.ultraThinMaterial)

            ScrollView {
                VStack(alignment: .leading, spacing: 24) {
                    // 星の名前とアイコン
                    HStack {
                        Image(systemName: "star.fill")
                            .font(.system(size: 40))
                            .foregroundColor(starColor)

                        VStack(alignment: .leading, spacing: 4) {
                            Text(star.name)
                                .font(.title)
                                .fontWeight(.bold)

                            if let spectralType = star.spectralType {
                                Text("スペクトル型: \(spectralType)")
                                    .font(.subheadline)
                                    .foregroundColor(.secondary)
                            }
                        }
                    }
                    .padding(.top, 8)

                    Divider()

                    // 明るさ
                    VStack(alignment: .leading, spacing: 8) {
                        Label("明るさ", systemImage: "sparkles")
                            .font(.headline)

                        HStack {
                            Text("等級:")
                                .foregroundColor(.secondary)
                            Text(String(format: "%.2f", star.magnitude))

                            Spacer()

                            Text(magnitudeDescription)
                                .font(.caption)
                                .padding(.horizontal, 12)
                                .padding(.vertical, 4)
                                .background(Color.blue.opacity(0.2))
                                .cornerRadius(8)
                        }
                    }

                    Divider()

                    // 物理的特徴
                    VStack(alignment: .leading, spacing: 8) {
                        Label("物理的特徴", systemImage: "atom")
                            .font(.headline)

                        VStack(spacing: 12) {
                            HStack {
                                Text("表面温度:")
                                    .foregroundColor(.secondary)
                                Spacer()
                                Text(starProperties.temperature)
                                    .monospacedDigit()
                            }

                            HStack {
                                Text("色:")
                                    .foregroundColor(.secondary)
                                Spacer()
                                HStack(spacing: 8) {
                                    Circle()
                                        .fill(starProperties.color)
                                        .frame(width: 20, height: 20)
                                    Text(starProperties.colorDescription)
                                }
                            }

                            HStack {
                                Text("種類:")
                                    .foregroundColor(.secondary)
                                Spacer()
                                Text(starTypeRefined)
                            }
                        }
                    }

                    Divider()

                    // 現在の位置
                    VStack(alignment: .leading, spacing: 8) {
                        Label("現在の位置", systemImage: "location.circle")
                            .font(.headline)

                        HStack {
                            VStack(alignment: .leading, spacing: 4) {
                                Text("高度")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                                Text(String(format: "%.1f°", star.altitude))
                                    .font(.title3)
                                    .monospacedDigit()
                            }

                            Spacer()

                            VStack(alignment: .leading, spacing: 4) {
                                Text("方位角")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                                Text(String(format: "%.1f°", star.azimuth))
                                    .font(.title3)
                                    .monospacedDigit()
                            }

                            Spacer()

                            VStack(alignment: .leading, spacing: 4) {
                                Text("方角")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                                Text(directionText)
                                    .font(.title3)
                            }
                        }
                    }

                    Divider()

                    // 天球座標
                    VStack(alignment: .leading, spacing: 8) {
                        Label("天球座標", systemImage: "globe.asia.australia")
                            .font(.headline)

                        VStack(spacing: 12) {
                            HStack {
                                Text("赤経 (RA):")
                                    .foregroundColor(.secondary)
                                Spacer()
                                Text(raText)
                                    .monospacedDigit()
                            }

                            HStack {
                                Text("赤緯 (Dec):")
                                    .foregroundColor(.secondary)
                                Spacer()
                                Text(decText)
                                    .monospacedDigit()
                            }
                        }
                    }

                    Spacer()
                }
                .padding()
            }
        }
        .frame(width: 360)
        .background(Color(uiColor: .systemBackground))
        .shadow(color: .black.opacity(0.3), radius: 20, x: -5, y: 0)
    }

    // MARK: - Computed Properties

    /// 星の物理的特徴
    private var starProperties: StarProperties {
        StarProperties(spectralType: star.spectralType)
    }

    /// 詳細な星のタイプ（光度階級を考慮）
    private var starTypeRefined: String {
        if let spectralType = star.spectralType {
            return StarProperties.refineStarType(spectralType: spectralType)
        }
        return starProperties.starType
    }

    /// 等級の説明
    private var magnitudeDescription: String {
        switch star.magnitude {
        case ..<0:
            return "最も明るい"
        case 0..<1:
            return "非常に明るい"
        case 1..<2:
            return "明るい"
        case 2..<3:
            return "よく見える"
        case 3..<4:
            return "見える"
        default:
            return "暗い"
        }
    }

    /// 星の色（スペクトル型に基づく）
    private var starColor: Color {
        guard let spectralType = star.spectralType?.uppercased().first else {
            return .white
        }

        switch spectralType {
        case "O", "B":
            return .blue
        case "A":
            return Color(red: 0.8, green: 0.9, blue: 1.0)
        case "F":
            return Color(red: 0.95, green: 0.95, blue: 0.8)
        case "G":
            return .yellow
        case "K":
            return .orange
        case "M":
            return .red
        default:
            return .white
        }
    }

    /// 方角のテキスト
    private var directionText: String {
        let azimuth = star.azimuth

        switch azimuth {
        case 337.5..<360, 0..<22.5:
            return "北"
        case 22.5..<67.5:
            return "北東"
        case 67.5..<112.5:
            return "東"
        case 112.5..<157.5:
            return "南東"
        case 157.5..<202.5:
            return "南"
        case 202.5..<247.5:
            return "南西"
        case 247.5..<292.5:
            return "西"
        case 292.5..<337.5:
            return "北西"
        default:
            return "—"
        }
    }

    /// 赤経のテキスト（時分秒形式）
    private var raText: String {
        let hours = star.visibleStar.star.coordinate.raToHours()
        let h = Int(hours)
        let m = Int((hours - Double(h)) * 60)
        let s = Int(((hours - Double(h)) * 60 - Double(m)) * 60)
        return String(format: "%02dh %02dm %02ds", h, m, s)
    }

    /// 赤緯のテキスト（度分秒形式）
    private var decText: String {
        let degrees = star.visibleStar.star.coordinate.decToDegrees()
        let sign = degrees >= 0 ? "+" : "−"
        let absDegrees = abs(degrees)
        let d = Int(absDegrees)
        let m = Int((absDegrees - Double(d)) * 60)
        let s = Int(((absDegrees - Double(d)) * 60 - Double(m)) * 60)
        return String(format: "%@%02d° %02d′ %02d″", sign, d, m, s)
    }
}

#Preview {
    StarDetailView(
        star: StarViewModel(
            visibleStar: VisibleStar(
                star: Star(
                    id: "HIP32349",
                    name: "Sirius",
                    coordinate: EquatorialCoordinate.Companion.shared.fromDegrees(
                        raDegrees: 101.287,
                        decDegrees: -16.716
                    ),
                    magnitude: -1.46,
                    spectralType: "A1V"
                ),
                altitudeDegrees: 45.0,
                azimuthDegrees: 180.0,
                hourAngle: 0.0
            ),
            screenSize: CGSize(width: 400, height: 800),
            viewportState: ViewportState()
        ),
        onDismiss: {}
    )
}
