//
//  AstraLoomService.swift
//  astra-loom
//

import Foundation
import shared

/// Astra Loom サービス
final class AstraLoomService {
    private let bridge: SwiftBridge

    init() {
        let manager = ManagerFactory.shared.createDefault()
        self.bridge = SwiftBridge(manager: manager)
    }

    /// 可視星を取得
    func getVisibleStars(
        observer: Observer,
        time: Date = Date(),
        maxMagnitude: Double = 4.0
    ) async throws -> [VisibleStar] {
        print("🚀 AstraLoomService: Getting visible stars...")
        print("   Observer lat: \(observer.latitude * 180/Double.pi)°, lon: \(observer.longitude * 180/Double.pi)°")
        print("   Time: \(time)")
        print("   MaxMagnitude: \(maxMagnitude)")

        let instant = time.toKotlinInstant()

        do {
            let stars = try await bridge.getVisibleStars(
                observer: observer,
                time: instant,
                maxMagnitude: maxMagnitude
            )
            print("✅ AstraLoomService: Got \(stars.count) visible stars")
            return stars
        } catch {
            print("❌ AstraLoomService: Error getting stars: \(error.localizedDescription)")
            throw error
        }
    }

    /// 可視星座を取得
    func getVisibleConstellations(
        observer: Observer,
        time: Date = Date()
    ) async throws -> [ConstellationWithStars] {
        print("🚀 AstraLoomService: Getting visible constellations...")

        let instant = time.toKotlinInstant()

        do {
            let constellations = try await bridge.getVisibleConstellations(
                observer: observer,
                time: instant
            )
            print("✅ AstraLoomService: Got \(constellations.count) visible constellations")
            return constellations
        } catch {
            print("❌ AstraLoomService: Error getting constellations: \(error.localizedDescription)")
            throw error
        }
    }

    /// 太陽の位置を取得
    func getSunPosition(
        observer: Observer,
        time: Date = Date()
    ) -> SunPosition {
        let instant = time.toKotlinInstant()
        return bridge.getSunPosition(observer: observer, time: instant)
    }

    /// 空の状態を取得
    func getSkyCondition(sunAltitude: Double) -> SkyCondition {
        return bridge.getSkyCondition(sunAltitude: sunAltitude)
    }
}
