# iOS実装ガイド - Astra Loom

このドキュメントは、iOS（iPadOS）アプリケーション実装を継続するための情報をまとめたものです。

## 📊 プロジェクト現状サマリー（2026-05-06時点）

### ✅ 完成している機能

#### 1. KMP Shared Module（完全実装済み）

**天文計算エンジン** (`astronomy/`)
- JulianDate, SiderealTime, CoordinateTransform, Precession, Refraction
- AstronomyEngine（統合API）
- 包括的なユニットテスト

**ドメインモデル** (`domain/`)
- Star, Constellation, Observer
- EquatorialCoordinate, HorizontalCoordinate

**データ層** (`data/`, `api/`)
- ローカルデータソース（JSON、25星 + 10星座）
- リモートデータソース（Ktor HTTP Client）
- 4つのキャッシング戦略（CACHE_FIRST, NETWORK_FIRST, CACHE_ONLY, NETWORK_ONLY）

**ユースケース** (`usecase/`)
- GetVisibleStarsUseCase
- GetConstellationStarsUseCase
- SearchStarsUseCase

**Manager層** (`manager/`) ← iOS統合の入口
- StarManager, ConstellationManager, AstronomyManager
- AstraLoomManager（トップレベルファサード）
- ManagerFactory（DI）

### 🎯 次のステップ：iOS実装

1. Xcodeプロジェクト作成（iPadOS向け）
2. KMP Frameworkの統合
3. Service層の実装（Swift）
4. TCAのセットアップ
5. UI実装（SwiftUI）

---

## 📁 ディレクトリ構成

```
astra-loom/
├── shared/                          # KMP共有モジュール（完成）
│   ├── src/
│   │   ├── commonMain/
│   │   │   ├── kotlin/com/astraloom/
│   │   │   │   ├── astronomy/      # 天文計算
│   │   │   │   ├── domain/         # ドメインモデル
│   │   │   │   ├── repository/     # リポジトリIF
│   │   │   │   ├── usecase/        # ユースケース
│   │   │   │   ├── data/           # データ層実装
│   │   │   │   ├── api/            # API層（Ktor）
│   │   │   │   ├── manager/        # Manager層 ★iOS統合ポイント
│   │   │   │   └── di/             # DI（ManagerFactory）
│   │   │   └── resources/
│   │   │       ├── stars.json
│   │   │       └── constellations.json
│   │   ├── iosMain/                # iOS固有実装
│   │   └── commonTest/             # テスト
│   ├── build.gradle.kts
│   └── README.md                   # Shared module詳細ドキュメント
│
├── iosApp/                         # ← これから作成
│   └── （Xcodeプロジェクト）
│
├── docs/
│   ├── requirements.md
│   ├── design.md
│   ├── architecture.md
│   └── ios-implementation-guide.md # このファイル
│
└── README.md
```

---

## 🔧 KMP Shared Moduleの使い方

### 1. Framework生成

```bash
cd /Users/masakifutami/Documents/00_開発/astra-loom
./gradlew :shared:embedAndSignAppleFrameworkForXcode
```

生成されるFramework:
```
shared/build/bin/iosSimulatorArm64/debugFramework/shared.framework
```

### 2. Xcodeプロジェクトへの統合

#### Step 1: Frameworkを追加
1. Xcodeでプロジェクトを開く
2. Target → General → Frameworks, Libraries, and Embedded Content
3. `+` → Add Other → Add Files
4. `shared.framework` を選択
5. Embed & Sign に設定

#### Step 2: Build Phaseの追加

Run Scriptを追加して、ビルド時にFrameworkを自動生成：

```bash
cd "$SRCROOT/.."
./gradlew :shared:embedAndSignAppleFrameworkForXcode
```

### 3. Swiftから使用

```swift
import shared

// ManagerFactoryでインスタンス生成
let manager = ManagerFactory.shared.createDefault()

// 観測地点の作成
let observer = Observer.Tokyo

// 可視星取得（Kotlin Coroutinesを使用）
Task {
    do {
        let result = try await manager.stars.getVisibleStars(
            observer: observer,
            time: KotlinInstant.companion.now(),
            maxMagnitude: 4.0
        )
        // result processing
    } catch {
        print("Error: \(error)")
    }
}
```

---

## 🏗️ iOS アーキテクチャ設計

### 全体構成

```
SwiftUI Views
    ↓
TCA (State/Action/Reducer)
    ↓
Dependencies (Services)
    ↓
Service Layer (Swift) ← ここを実装
    ↓
KMP Manager Layer (Kotlin)
```

### Service層の役割

1. **Kotlin → Swift 変換**
   - Kotlin Coroutines → Swift async/await
   - Kotlin Result → Swift throws
   - Kotlin types → Swift types

2. **TCA Dependencies対応**
   - DependencyKey protocol準拠
   - Mock可能なインターフェース

3. **エラーハンドリング**
   - Kotlin例外 → Swift Error型

### 推奨ディレクトリ構成

```
iosApp/AstraLoom/
├── App/
│   ├── AstraLoomApp.swift              # @main
│   └── AppReducer.swift                # Root reducer
│
├── Features/                           # 機能別（TCA）
│   ├── SkyView/
│   │   ├── SkyViewFeature.swift       # State/Action/Reducer
│   │   └── SkyViewView.swift          # SwiftUI View
│   ├── ConstellationList/
│   └── Settings/
│
├── Services/                           # KMPラッパー
│   ├── AstraLoomService.swift         # 統合Service
│   ├── StarService.swift              # 星関連
│   ├── ConstellationService.swift     # 星座関連
│   └── ServiceProtocols.swift         # Protocol定義
│
├── Models/
│   └── Extensions/
│       ├── Observer+Extensions.swift  # KMP型の拡張
│       └── Star+Extensions.swift
│
├── Utilities/
│   ├── KotlinBridge.swift             # Kotlin変換ヘルパー
│   └── Logger.swift
│
└── Resources/
    └── Assets.xcassets
```

---

## 📝 Service層実装テンプレート

### ServiceProtocols.swift

```swift
import Foundation
import shared

// MARK: - Service Protocol
protocol AstraLoomService {
    func getVisibleStars(
        observer: Observer,
        time: Date,
        maxMagnitude: Double
    ) async throws -> [VisibleStar]

    func getConstellation(
        id: String,
        observer: Observer,
        time: Date
    ) async throws -> ConstellationWithStars

    func getNightSkySummary(
        observer: Observer,
        time: Date
    ) async throws -> NightSkySummary
}

// MARK: - Service Implementation
final class AstraLoomServiceImpl: AstraLoomService {
    private let manager: AstraLoomManager

    init(manager: AstraLoomManager) {
        self.manager = manager
    }

    func getVisibleStars(
        observer: Observer,
        time: Date,
        maxMagnitude: Double
    ) async throws -> [VisibleStar] {
        // Date → Instant変換
        let instant = time.toKotlinInstant()

        // Kotlin Coroutinesをawait
        return try await withCheckedThrowingContinuation { continuation in
            manager.stars.getVisibleStars(
                observer: observer,
                time: instant,
                maxMagnitude: maxMagnitude
            ) { result in
                if let stars = result?.getOrNull() {
                    continuation.resume(returning: Array(stars))
                } else if let error = result?.exceptionOrNull() {
                    continuation.resume(throwing: error)
                } else {
                    continuation.resume(throwing: ServiceError.unknown)
                }
            }
        }
    }
}

// MARK: - Errors
enum ServiceError: Error {
    case unknown
    case kotlinException(Error)
    case conversionFailed
}
```

### KotlinBridge.swift（変換ヘルパー）

```swift
import Foundation
import shared

extension Date {
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
    func toSwiftDate() -> Date {
        return Date(timeIntervalSince1970: Double(self.epochSeconds))
    }
}

extension Observer {
    static func fromLocation(_ latitude: Double, _ longitude: Double) -> Observer {
        return Observer.companion.fromDegrees(
            latitudeDegrees: latitude,
            longitudeDegrees: longitude,
            elevationMeters: 0.0
        )
    }
}
```

---

## 🎨 TCA統合ガイド

### Dependencies設定

```swift
import ComposableArchitecture
import shared

// MARK: - Dependency Key
extension AstraLoomService: DependencyKey {
    static var liveValue: AstraLoomService {
        let manager = ManagerFactory.shared.createDefault()
        return AstraLoomServiceImpl(manager: manager)
    }

    static var testValue: AstraLoomService {
        // Mock implementation
        MockAstraLoomService()
    }
}

extension DependencyValues {
    var astraLoomService: AstraLoomService {
        get { self[AstraLoomService.self] }
        set { self[AstraLoomService.self] = newValue }
    }
}
```

### Feature実装例

```swift
import ComposableArchitecture

@Reducer
struct SkyViewFeature {
    struct State: Equatable {
        var visibleStars: [VisibleStar] = []
        var observer: Observer = .Tokyo
        var isLoading = false
        var errorMessage: String?
    }

    enum Action {
        case onAppear
        case loadStars
        case starsLoaded([VisibleStar])
        case loadFailed(Error)
    }

    @Dependency(\.astraLoomService) var service

    var body: some ReducerOf<Self> {
        Reduce { state, action in
            switch action {
            case .onAppear:
                return .send(.loadStars)

            case .loadStars:
                state.isLoading = true
                return .run { [observer = state.observer] send in
                    do {
                        let stars = try await service.getVisibleStars(
                            observer: observer,
                            time: Date(),
                            maxMagnitude: 4.0
                        )
                        await send(.starsLoaded(stars))
                    } catch {
                        await send(.loadFailed(error))
                    }
                }

            case let .starsLoaded(stars):
                state.isLoading = false
                state.visibleStars = stars
                return .none

            case let .loadFailed(error):
                state.isLoading = false
                state.errorMessage = error.localizedDescription
                return .none
            }
        }
    }
}
```

---

## 📦 必要な依存関係

### Package.swift（Swift Package Manager）

```swift
// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "AstraLoom",
    platforms: [.iOS(.v17)],
    dependencies: [
        .package(
            url: "https://github.com/pointfreeco/swift-composable-architecture",
            from: "1.9.0"
        )
    ],
    targets: [
        .target(
            name: "AstraLoom",
            dependencies: [
                .product(name: "ComposableArchitecture", package: "swift-composable-architecture")
            ]
        )
    ]
)
```

または **CocoaPods**:

```ruby
platform :ios, '17.0'
use_frameworks!

target 'AstraLoom' do
  pod 'ComposableArchitecture', '~> 1.9'
end
```

---

## 🚀 次のステップ実装順序

### Phase 1: プロジェクトセットアップ
1. ✅ Xcodeプロジェクト作成（あなたが実施）
2. KMP Frameworkの統合
3. TCA依存関係追加
4. 基本的なディレクトリ構成

### Phase 2: Service層実装
1. ServiceProtocols.swift
2. AstraLoomServiceImpl.swift
3. KotlinBridge.swift（変換ヘルパー）
4. TCA Dependencies設定

### Phase 3: 最初のFeature実装
1. SkyViewFeature（メイン星空表示）
   - State/Action/Reducer
   - SwiftUI View
   - 星の描画（Canvas）

### Phase 4: 追加機能
1. ConstellationListFeature（星座リスト）
2. StarDetailFeature（星詳細）
3. SettingsFeature（設定）

---

## 🔗 重要なリンク

- **GitHubリポジトリ**: https://github.com/masa-futa/astra-loom
- **Shared Module README**: `shared/README.md`
- **設計ドキュメント**: `docs/`

---

## 💡 重要な設計決定事項

### 1. KMP使用ルール

**DO（推奨）:**
- ManagerFactoryを使ってインスタンス生成
- Service層でKotlin型をSwift型に変換
- async/awaitでCoroutinesをラップ

**DON'T（非推奨）:**
- SwiftUIから直接KMP呼び出し
- Kotlin型をそのままStateに保存
- 同期的なAPI呼び出し

### 2. エラーハンドリング

- Kotlin Result → Swift throws変換
- すべてのService呼び出しは`async throws`
- TCAでは`.run`エフェクト内でエラーハンドリング

### 3. 座標系

- KMPは内部でラジアン使用
- SwiftUIは度（degrees）を推奨
- 変換は必ずService層で実施

### 4. 観測地点

- デフォルト: `Observer.Tokyo`
- GPS取得後はユーザー位置に更新
- 設定で手動入力も可能

---

## 📞 次のセッションで伝えること

1. **Xcodeプロジェクトのパス**
   - 例: `/Users/masakifutami/Documents/00_開発/astra-loom/iosApp/AstraLoom.xcodeproj`

2. **実装したいFeature**
   - 最初は SkyViewFeature（星空表示）を推奨

3. **進行状況**
   - どこまで実装したか
   - 遭遇した問題

4. **質問事項**
   - KMP統合で困っていること
   - TCAの設計で迷っていること

---

**作成日**: 2026-05-06
**最終更新**: 2026-05-06
**バージョン**: 1.0
**プロジェクト**: Astra Loom
