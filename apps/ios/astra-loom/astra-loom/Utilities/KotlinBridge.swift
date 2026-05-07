//
//  KotlinBridge.swift
//  astra-loom
//
//  Kotlin Multiplatform ↔ Swift 型変換ヘルパー
//

import Foundation
import shared

// MARK: - Date ↔ Instant 変換

extension Date {
    /// SwiftのDateをKotlinのInstantに変換
    func toKotlinInstant() -> Instant {
        let timeInterval = self.timeIntervalSince1970
        let seconds = Int64(timeInterval)
        let nanoseconds = Int32((timeInterval - Double(seconds)) * 1_000_000_000)
        return Instant.companion.fromEpochSeconds(
            epochSeconds: seconds,
            nanosecondAdjustment: nanoseconds
        )
    }
}

extension Instant {
    /// KotlinのInstantをSwiftのDateに変換
    func toSwiftDate() -> Date {
        return Date(timeIntervalSince1970: Double(self.epochSeconds))
    }
}

// MARK: - Observer ヘルパー

extension Observer {
    /// 緯度経度からObserverを作成
    static func from(latitude: Double, longitude: Double, elevation: Double = 0.0) -> Observer {
        return Observer.companion.fromDegrees(
            latitudeDegrees: latitude,
            longitudeDegrees: longitude,
            elevationMeters: elevation
        )
    }

    /// 度数での緯度を取得
    var latitudeDegrees: Double {
        return self.latitudeToDegrees()
    }

    /// 度数での経度を取得
    var longitudeDegrees: Double {
        return self.longitudeToDegrees()
    }
}

// MARK: - EquatorialCoordinate ヘルパー

extension EquatorialCoordinate {
    /// 赤経を時間単位で取得
    var raHours: Double {
        return self.raToHours()
    }

    /// 赤緯を度数で取得
    var decDegrees: Double {
        return self.decToDegrees()
    }
}

// MARK: - HorizontalCoordinate ヘルパー

extension HorizontalCoordinate {
    /// 高度を度数で取得
    var altitudeDegrees: Double {
        return self.altitudeToDegrees()
    }

    /// 方位角を度数で取得
    var azimuthDegrees: Double {
        return self.azimuthToDegrees()
    }
}

// MARK: - Kotlin Result → Swift throws 変換
// Note: Kotlin Result型は直接使用せず、コールバックで処理

// MARK: - エラー型

enum KotlinError: Error, LocalizedError {
    case unknown
    case kotlinException(Error)
    case conversionFailed(String)

    var errorDescription: String? {
        switch self {
        case .unknown:
            return "Unknown error occurred in Kotlin module"
        case .kotlinException(let error):
            return "Kotlin error: \(error.localizedDescription)"
        case .conversionFailed(let message):
            return "Type conversion failed: \(message)"
        }
    }
}

// MARK: - Array 変換ヘルパー

extension Array where Element: AnyObject {
    /// KotlinのKotlinArray/NSArrayをSwift Arrayに変換
    func toSwiftArray() -> [Element] {
        return Array(self)
    }
}
