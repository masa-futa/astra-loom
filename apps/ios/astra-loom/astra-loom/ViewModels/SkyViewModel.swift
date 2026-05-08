import SwiftUI
import Combine
import shared

/// View model for the entire sky view
@MainActor
final class SkyViewModel: ObservableObject {
    @Published var stars: [StarViewModel] = []
    @Published var constellations: [ConstellationWithStars] = []
    @Published var gradient: [Color] = []
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var sunAltitude: Double = 0.0
    @Published var skyCondition: String = ""

    // 観測設定
    @Published var currentLocation: LocationPreset = .tokyo
    @Published var currentTime: Date = Date()
    @Published var useCurrentTime: Bool = true

    // ビューポート設定
    @Published var viewportState = ViewportState()

    // 星データのキャッシュ（ビューポート変更時の再計算用）
    private var visibleStarsData: [VisibleStar] = []
    private var currentScreenSize: CGSize = .zero

    private let service: AstraLoomService

    init(service: AstraLoomService) {
        self.service = service
        self.gradient = Self.createGradient(for: Date(), sunAltitude: 0.0)
    }

    /// 観測地点を変更（その地点の現在時刻に切り替え）
    func changeLocation(_ location: LocationPreset) {
        currentLocation = location

        // ユーザーが明示的に時刻を固定していない場合のみ、現在時刻に戻す
        if useCurrentTime {
            currentTime = Date()
        }
        // useCurrentTime = false の場合は、ユーザーが設定した時刻を維持
    }

    /// 時刻を変更
    func changeTime(_ time: Date) {
        currentTime = time
        useCurrentTime = false
    }

    /// 現在時刻に戻す
    func resetToCurrentTime() {
        currentTime = Date()
        useCurrentTime = true
    }

    /// ビューポート変更時に星の座標のみを再計算（高速）
    func updateStarPositions() {
        guard !visibleStarsData.isEmpty, currentScreenSize != .zero else { return }

        self.stars = visibleStarsData.map { star in
            StarViewModel(
                visibleStar: star,
                screenSize: currentScreenSize,
                viewportState: self.viewportState
            )
        }
    }

    /// Load visible stars for current location and time
    func loadStars(screenSize: CGSize) async {
        isLoading = true
        errorMessage = nil

        let observer = currentLocation.observer
        // 現在時刻モードの場合は、毎回最新の時刻を取得
        let time: Date
        if useCurrentTime {
            time = Date()
            currentTime = time  // 表示用に更新
        } else {
            time = currentTime
        }

        print("🌟 Loading stars for location: \(currentLocation.nameJa)")
        print("🌟 Screen size: \(screenSize)")
        print("🌟 Time: \(time)")
        print("🌟 Use current time: \(useCurrentTime)")

        do {
            // Calculate sun position
            let sunPosition = service.getSunPosition(observer: observer, time: time)
            self.sunAltitude = sunPosition.altitudeDegrees
            let condition = service.getSkyCondition(sunAltitude: sunPosition.altitudeDegrees)
            self.skyCondition = skyConditionText(condition)

            print("🌞 Sun altitude: \(sunPosition.altitudeDegrees)°")
            print("🌞 Sky condition: \(skyConditionText(condition))")

            let visibleStars = try await service.getVisibleStars(
                observer: observer,
                time: time,
                maxMagnitude: 4.5
            )

            print("⭐️ Received \(visibleStars.count) visible stars")

            // キャッシュに保存
            self.visibleStarsData = visibleStars
            self.currentScreenSize = screenSize

            // Convert to view models
            self.stars = visibleStars.map { star in
                StarViewModel(
                    visibleStar: star,
                    screenSize: screenSize,
                    viewportState: self.viewportState
                )
            }

            print("✅ Converted to \(self.stars.count) star view models")

            // Load constellations
            do {
                let constellations = try await service.getVisibleConstellations(
                    observer: observer,
                    time: time
                )
                self.constellations = constellations
                print("✅ Loaded \(constellations.count) visible constellations")
            } catch {
                print("⚠️ Warning: Failed to load constellations: \(error.localizedDescription)")
                // Don't fail the whole load if constellations fail
                self.constellations = []
            }

            // Update gradient based on sun altitude
            self.gradient = Self.createGradient(for: time, sunAltitude: sunPosition.altitudeDegrees)

            isLoading = false
        } catch {
            print("❌ Error loading stars: \(error.localizedDescription)")
            errorMessage = "星データの読み込みに失敗しました: \(error.localizedDescription)"
            isLoading = false
        }
    }

    private func skyConditionText(_ condition: SkyCondition) -> String {
        switch condition {
        case .day: return "昼間"
        case .civilTwilight: return "薄明"
        case .nauticalTwilight: return "航海薄明"
        case .astronomicalTwilight: return "天文薄明"
        case .night: return "夜間"
        default: return "不明"
        }
    }

    /// Create gradient colors based on sun altitude
    static func createGradient(for time: Date, sunAltitude: Double) -> [Color] {
        // Define gradient colors
        let deepBlue = Color(red: 15/255, green: 52/255, blue: 96/255)
        let midnightBlue = Color(red: 10/255, green: 25/255, blue: 41/255)
        let darkNavy = Color(red: 22/255, green: 33/255, blue: 62/255)
        let almostBlack = Color.black
        let orange = Color(red: 255/255, green: 140/255, blue: 0/255)
        let purple = Color(red: 138/255, green: 43/255, blue: 226/255)
        let pink = Color(red: 255/255, green: 105/255, blue: 180/255)

        // Daytime colors
        let skyBlue = Color(red: 135/255, green: 206/255, blue: 235/255)
        let lightBlue = Color(red: 176/255, green: 224/255, blue: 230/255)
        let paleBlue = Color(red: 215/255, green: 235/255, blue: 250/255)

        // Sun altitude based gradients
        switch sunAltitude {
        case 10...: // High sun - bright day
            return [lightBlue, skyBlue, paleBlue, lightBlue]

        case 0..<10: // Low sun - near horizon
            return [orange, pink, lightBlue, skyBlue]

        case -6..<0: // Civil twilight
            return [orange, purple, deepBlue, midnightBlue]

        case -12..<(-6): // Nautical twilight
            return [purple, deepBlue, midnightBlue, almostBlack]

        case -18..<(-12): // Astronomical twilight
            return [deepBlue, midnightBlue, almostBlack]

        default: // Deep night (sun < -18°)
            return [darkNavy, deepBlue, midnightBlue, almostBlack]
        }
    }
}
