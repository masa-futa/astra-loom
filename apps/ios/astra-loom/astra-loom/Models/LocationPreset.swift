import Foundation
import shared

/// 観測地点のプリセット
struct LocationPreset: Identifiable, Equatable {
    let id: String
    let name: String
    let nameJa: String
    let countryCode: String
    let observer: Observer
    let timezone: Foundation.TimeZone

    // Equatable conformance - compare by ID
    static func == (lhs: LocationPreset, rhs: LocationPreset) -> Bool {
        return lhs.id == rhs.id
    }

    /// 現在、この地点で星空観測に適しているかスコアリング
    func getStargazingScore(at time: Date, sunCalculator: SunCalculator) -> Double {
        let sunPos = sunCalculator.calculateSunPosition(observer: observer, time: time.toKotlinInstant())
        let sunAlt = sunPos.altitudeDegrees

        // 太陽高度が低いほどスコアが高い
        // -18° 以下（天文薄明終了）で最高スコア
        if sunAlt < -18 {
            return 100.0
        } else if sunAlt < -12 {
            return 80.0 - (sunAlt + 12) * 3.33  // -12° to -18°
        } else if sunAlt < -6 {
            return 60.0 - (sunAlt + 6) * 3.33   // -6° to -12°
        } else if sunAlt < 0 {
            return 40.0 - (sunAlt) * 6.67       // 0° to -6°
        } else {
            return max(0, 40.0 - sunAlt * 2)    // Above horizon
        }
    }

    /// この地点の現地時刻を取得
    func currentLocalTime() -> Date {
        return Date()
    }

    /// この地点で星空観測に最適な時刻を取得（夜21時）
    func bestStargazingTime() -> Date {
        let calendar = Calendar.current
        var components = calendar.dateComponents(in: timezone, from: Date())
        components.hour = 21
        components.minute = 0
        components.second = 0

        return calendar.date(from: components) ?? Date()
    }

    /// この地点が現在夜間かどうか（太陽高度 < 0°）
    func isNighttime(sunCalculator: SunCalculator) -> Bool {
        let sunPos = sunCalculator.calculateSunPosition(observer: observer, time: Date().toKotlinInstant())
        return sunPos.altitudeDegrees < 0
    }
}

extension LocationPreset {
    /// 世界の主要都市プリセット
    static let presets: [LocationPreset] = [
        // アジア
        LocationPreset(
            id: "tokyo",
            name: "Tokyo",
            nameJa: "東京",
            countryCode: "JP",
            observer: Observer.companion.Tokyo,
            timezone: Foundation.TimeZone(identifier: "Asia/Tokyo")!
        ),
        LocationPreset(
            id: "beijing",
            name: "Beijing",
            nameJa: "北京",
            countryCode: "CN",
            observer: Observer.companion.fromDegrees(
                latitudeDegrees: 39.9042,
                longitudeDegrees: 116.4074,
                elevationMeters: 43.0
            ),
            timezone: Foundation.TimeZone(identifier: "Asia/Shanghai")!
        ),
        LocationPreset(
            id: "singapore",
            name: "Singapore",
            nameJa: "シンガポール",
            countryCode: "SG",
            observer: Observer.companion.fromDegrees(
                latitudeDegrees: 1.3521,
                longitudeDegrees: 103.8198,
                elevationMeters: 15.0
            ),
            timezone: Foundation.TimeZone(identifier: "Asia/Singapore")!
        ),

        // ヨーロッパ
        LocationPreset(
            id: "london",
            name: "London",
            nameJa: "ロンドン",
            countryCode: "GB",
            observer: Observer.companion.London,
            timezone: Foundation.TimeZone(identifier: "Europe/London")!
        ),
        LocationPreset(
            id: "paris",
            name: "Paris",
            nameJa: "パリ",
            countryCode: "FR",
            observer: Observer.companion.fromDegrees(
                latitudeDegrees: 48.8566,
                longitudeDegrees: 2.3522,
                elevationMeters: 35.0
            ),
            timezone: Foundation.TimeZone(identifier: "Europe/Paris")!
        ),
        LocationPreset(
            id: "rome",
            name: "Rome",
            nameJa: "ローマ",
            countryCode: "IT",
            observer: Observer.companion.fromDegrees(
                latitudeDegrees: 41.9028,
                longitudeDegrees: 12.4964,
                elevationMeters: 21.0
            ),
            timezone: Foundation.TimeZone(identifier: "Europe/Rome")!
        ),

        // 北米
        LocationPreset(
            id: "newyork",
            name: "New York",
            nameJa: "ニューヨーク",
            countryCode: "US",
            observer: Observer.companion.NewYork,
            timezone: Foundation.TimeZone(identifier: "America/New_York")!
        ),
        LocationPreset(
            id: "losangeles",
            name: "Los Angeles",
            nameJa: "ロサンゼルス",
            countryCode: "US",
            observer: Observer.companion.fromDegrees(
                latitudeDegrees: 34.0522,
                longitudeDegrees: -118.2437,
                elevationMeters: 71.0
            ),
            timezone: Foundation.TimeZone(identifier: "America/Los_Angeles")!
        ),

        // 南米
        LocationPreset(
            id: "riodejaneiro",
            name: "Rio de Janeiro",
            nameJa: "リオデジャネイロ",
            countryCode: "BR",
            observer: Observer.companion.fromDegrees(
                latitudeDegrees: -22.9068,
                longitudeDegrees: -43.1729,
                elevationMeters: 2.0
            ),
            timezone: Foundation.TimeZone(identifier: "America/Sao_Paulo")!
        ),

        // オセアニア
        LocationPreset(
            id: "sydney",
            name: "Sydney",
            nameJa: "シドニー",
            countryCode: "AU",
            observer: Observer.companion.fromDegrees(
                latitudeDegrees: -33.8688,
                longitudeDegrees: 151.2093,
                elevationMeters: 3.0
            ),
            timezone: Foundation.TimeZone(identifier: "Australia/Sydney")!
        ),

        // アフリカ
        LocationPreset(
            id: "capetown",
            name: "Cape Town",
            nameJa: "ケープタウン",
            countryCode: "ZA",
            observer: Observer.companion.fromDegrees(
                latitudeDegrees: -33.9249,
                longitudeDegrees: 18.4241,
                elevationMeters: 25.0
            ),
            timezone: Foundation.TimeZone(identifier: "Africa/Johannesburg")!
        ),

        // 天文観測に適した場所
        LocationPreset(
            id: "maunakea",
            name: "Mauna Kea",
            nameJa: "マウナケア",
            countryCode: "US",
            observer: Observer.companion.fromDegrees(
                latitudeDegrees: 19.8207,
                longitudeDegrees: -155.4681,
                elevationMeters: 4207.0
            ),
            timezone: Foundation.TimeZone(identifier: "Pacific/Honolulu")!
        ),
        LocationPreset(
            id: "atacama",
            name: "Atacama",
            nameJa: "アタカマ",
            countryCode: "CL",
            observer: Observer.companion.fromDegrees(
                latitudeDegrees: -23.0,
                longitudeDegrees: -67.0,
                elevationMeters: 5000.0
            ),
            timezone: Foundation.TimeZone(identifier: "America/Santiago")!
        )
    ]

    /// IDで地点を検索
    static func findById(_ id: String) -> LocationPreset? {
        return presets.first { $0.id == id }
    }

    /// デフォルトの地点（東京）
    static var tokyo: LocationPreset {
        return presets.first { $0.id == "tokyo" }!
    }
}
