# Astra Loom Shared Module (KMP)

このモジュールは、Astra Loomのコアビジネスロジックを含むKotlin Multiplatform共有モジュールです。iOS、Androidの両プラットフォームで使用されます。

## 📁 ディレクトリ構成

```
shared/
├── src/
│   ├── commonMain/kotlin/com/astraloom/
│   │   ├── astronomy/          # 天文計算エンジン
│   │   │   ├── AstronomyEngine.kt
│   │   │   ├── CoordinateTransform.kt
│   │   │   ├── JulianDate.kt
│   │   │   ├── Precession.kt
│   │   │   ├── Refraction.kt
│   │   │   └── SiderealTime.kt
│   │   │
│   │   ├── domain/             # ドメインモデル
│   │   │   ├── Constellation.kt
│   │   │   ├── EquatorialCoordinate.kt
│   │   │   ├── HorizontalCoordinate.kt
│   │   │   ├── Observer.kt
│   │   │   └── Star.kt
│   │   │
│   │   ├── repository/         # リポジトリインターフェース
│   │   │   ├── ConstellationRepository.kt
│   │   │   └── StarRepository.kt
│   │   │
│   │   ├── usecase/            # ユースケース（アプリケーションロジック）
│   │   │   ├── GetConstellationStarsUseCase.kt
│   │   │   ├── GetVisibleStarsUseCase.kt
│   │   │   └── SearchStarsUseCase.kt
│   │   │
│   │   ├── data/               # データ層実装
│   │   │   ├── model/          # DTOモデル
│   │   │   │   ├── ConstellationDto.kt
│   │   │   │   └── StarDto.kt
│   │   │   │
│   │   │   ├── source/         # データソース
│   │   │   │   ├── LocalConstellationDataSource.kt
│   │   │   │   ├── LocalStarDataSource.kt
│   │   │   │   └── ResourceReader.kt (expect)
│   │   │   │
│   │   │   ├── repository/     # リポジトリ実装
│   │   │   │   ├── LocalConstellationRepository.kt
│   │   │   │   ├── LocalStarRepository.kt
│   │   │   │   ├── CachedConstellationRepository.kt
│   │   │   │   └── CachedStarRepository.kt
│   │   │   │
│   │   │   └── cache/          # キャッシング
│   │   │       └── CacheStrategy.kt
│   │   │
│   │   └── api/                # API層（リモートデータアクセス）
│   │       ├── KtorClient.kt (expect)
│   │       ├── ApiEndpoints.kt
│   │       │
│   │       ├── model/          # APIモデル
│   │       │   ├── ApiException.kt
│   │       │   └── ApiResponse.kt
│   │       │
│   │       ├── source/         # リモートデータソース
│   │       │   ├── RemoteConstellationDataSource.kt
│   │       │   └── RemoteStarDataSource.kt
│   │       │
│   │       └── repository/     # リモートリポジトリ実装
│   │           ├── RemoteConstellationRepository.kt
│   │           └── RemoteStarRepository.kt
│   │
│   ├── commonMain/resources/
│   │   ├── stars.json          # 星カタログデータ（25星）
│   │   └── constellations.json # 星座データ（10星座）
│   │
│   ├── commonTest/kotlin/com/astraloom/
│   │   └── astronomy/          # 天文計算のユニットテスト
│   │       ├── AstronomyEngineTest.kt
│   │       ├── CoordinateTransformTest.kt
│   │       ├── JulianDateTest.kt
│   │       ├── PrecessionTest.kt
│   │       ├── RefractionTest.kt
│   │       └── SiderealTimeTest.kt
│   │
│   ├── iosMain/kotlin/com/astraloom/
│   │   ├── api/
│   │   │   └── KtorClient.kt (actual - Darwin engine)
│   │   └── data/source/
│   │       └── ResourceReader.kt (actual - NSBundle)
│   │
│   └── androidMain/kotlin/com/astraloom/
│       ├── api/
│       │   └── KtorClient.kt (actual - Android engine)
│       └── data/source/
│           └── ResourceReader.kt (actual - Assets)
│
└── build.gradle.kts
```

## 🏗️ アーキテクチャ

### レイヤー構成（Clean Architecture）

```
┌─────────────────────────────────────────────────────┐
│                  Presentation Layer                 │
│              (iOS: SwiftUI / Android: Compose)      │
│                   ※ このモジュール外                  │
└─────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────┐
│              Use Case Layer (usecase/)              │
│  - GetVisibleStarsUseCase                          │
│  - GetConstellationStarsUseCase                    │
│  - SearchStarsUseCase                              │
└─────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────┐
│           Domain Layer (domain/, astronomy/)        │
│  - Star, Constellation, Observer                   │
│  - EquatorialCoordinate, HorizontalCoordinate      │
│  - AstronomyEngine (天文計算)                        │
└─────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────┐
│      Repository Interface (repository/)             │
│  - StarRepository                                   │
│  - ConstellationRepository                          │
└─────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────┐
│      Repository Implementation (data/, api/)        │
│  ┌─────────────────┬───────────────┬──────────────┐ │
│  │ Local           │ Remote        │ Cached       │ │
│  │ (JSON)          │ (Ktor API)    │ (Strategy)   │ │
│  └─────────────────┴───────────────┴──────────────┘ │
└─────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────┐
│            Data Source (data/source, api/source)    │
│  - LocalDataSource (JSON)                           │
│  - RemoteDataSource (HTTP)                          │
└─────────────────────────────────────────────────────┘
```

## 📦 各モジュールの役割

### 1. `astronomy/` - 天文計算エンジン

**役割**: 天文学的に正確な星の位置計算

**主要クラス**:
- `AstronomyEngine` - 統合APIファサード
- `JulianDate` - ユリウス日計算
- `SiderealTime` - 恒星時計算（GMST, LST）
- `CoordinateTransform` - 座標変換（RA/Dec ↔ Alt/Az）
- `Precession` - 歳差補正（IAU 1976）
- `Refraction` - 大気差補正（Bennett式）

**特徴**:
- Pure Function（副作用なし）
- すべてDouble型（ラジアン）で統一
- テスト容易性を重視
- Stellariumとの視覚的一致を目標

### 2. `domain/` - ドメインモデル

**役割**: ビジネスドメインの概念をモデル化

**主要クラス**:
- `Star` - 恒星（位置、明るさ、名前）
- `Constellation` - 星座（星のリスト、線）
- `Observer` - 観測者（緯度、経度、標高）
- `EquatorialCoordinate` - 赤道座標（RA/Dec）
- `HorizontalCoordinate` - 地平座標（Alt/Az）

**特徴**:
- イミュータブル（data class）
- バリデーションロジック内蔵
- 変換メソッド提供（度 ↔ ラジアン）

### 3. `repository/` - リポジトリインターフェース

**役割**: データアクセスの抽象化

**主要インターフェース**:
- `StarRepository` - 星データへのアクセス
- `ConstellationRepository` - 星座データへのアクセス

**特徴**:
- suspend関数（Coroutines対応）
- Result型でエラーハンドリング
- 実装に依存しない抽象化

### 4. `usecase/` - ユースケース層

**役割**: アプリケーション固有のビジネスロジック

**主要クラス**:
- `GetVisibleStarsUseCase` - 可視星取得＋位置計算
- `GetConstellationStarsUseCase` - 星座の星取得＋位置計算
- `SearchStarsUseCase` - 星検索

**特徴**:
- Repository + AstronomyEngine の統合
- UIに最適化されたデータ形式で返却
- 複雑なビジネスロジックをカプセル化

### 5. `data/` - データ層実装

**役割**: データの永続化とアクセス

**構成**:
- `model/` - DTO（Data Transfer Object）
- `source/` - データソース（Local）
- `repository/` - リポジトリ実装（Local, Cached）
- `cache/` - キャッシング戦略

**特徴**:
- ローカルデータソース（JSON）
- キャッシング戦略（4種類）
- プラットフォーム固有実装（expect/actual）

### 6. `api/` - API層

**役割**: リモートデータアクセス

**構成**:
- `KtorClient` - HTTP クライアント
- `source/` - リモートデータソース
- `repository/` - リモートリポジトリ実装
- `model/` - API モデル

**特徴**:
- Ktor 2.3.8 使用
- タイムアウト、リトライ設定
- エラーハンドリング（型付き例外）

## 🔄 データフロー例

### 可視星取得の流れ

```kotlin
// 1. UI層から呼び出し
val useCase = GetVisibleStarsUseCase(repository, astronomyEngine)
val result = useCase.execute(observer, time)

// 2. UseCase内部
suspend fun execute(...): Result<List<VisibleStar>> {
    // 2.1 リポジトリから星データ取得
    val stars = repository.getStarsByMagnitude(4.0)

    // 2.2 天文計算エンジンで位置計算
    val positions = astronomyEngine.calculateVisibleStars(stars, observer, time)

    // 2.3 UIに最適化した形式で返却
    return positions.map { ... }
}

// 3. Repository（Cachedの場合）
suspend fun getStarsByMagnitude(...): Result<List<Star>> {
    // 3.1 キャッシュチェック
    cache.get(key)?.let { return Result.success(it) }

    // 3.2 ローカルデータソース
    localRepository.getStarsByMagnitude(...)
        .onSuccess { cache.put(key, it); return Result.success(it) }

    // 3.3 リモートデータソース（フォールバック）
    remoteRepository.getStarsByMagnitude(...)
        .onSuccess { cache.put(key, it) }
}

// 4. DataSource
suspend fun loadStars(): Result<List<Star>> {
    // 4.1 JSON読み込み（iOS: NSBundle / Android: Assets）
    val json = resourceReader.readResource("stars.json")

    // 4.2 パース
    val dto = Json.decodeFromString<StarCatalogDto>(json)

    // 4.3 ドメインモデルに変換
    return Result.success(dto.stars.map { it.toDomain() })
}
```

## ⚠️ 今後の実装時の注意点

### 1. expect/actual パターン

**注意**: プラットフォーム固有の実装が必要な機能

```kotlin
// commonMain (expect宣言)
expect class ResourceReader() {
    fun readResource(path: String): String
}

// iosMain (actual実装)
actual class ResourceReader {
    actual fun readResource(path: String): String {
        // NSBundle実装
    }
}

// androidMain (actual実装)
actual class ResourceReader {
    actual fun readResource(path: String): String {
        // Assets実装
    }
}
```

**ルール**:
- expect は commonMain に配置
- actual は各プラットフォーム（iosMain, androidMain）に配置
- シグネチャは完全一致が必要

### 2. 天文計算の精度

**注意**: すべてラジアンで計算し、最後に度に変換

```kotlin
// ✅ 正しい例
val raRadians = Math.toRadians(raDegrees)
val result = calculateSomething(raRadians)
val resultDegrees = Math.toDegrees(result)

// ❌ 間違った例
val result = calculateSomething(raDegrees) // 度で計算してはいけない
```

**ルール**:
- 内部計算はすべてラジアン
- APIの入出力のみ度を許可
- 角度の範囲チェックを忘れずに

### 3. Result型の使用

**注意**: 例外ではなくResult型で処理

```kotlin
// ✅ 正しい例
suspend fun getData(): Result<List<Star>> {
    return try {
        Result.success(fetchData())
    } catch (e: Exception) {
        Result.failure(e)
    }
}

// 使用側
getData()
    .onSuccess { stars -> /* 処理 */ }
    .onFailure { error -> /* エラー処理 */ }

// ❌ 間違った例
suspend fun getData(): List<Star> {
    return fetchData() // 例外が外に漏れる
}
```

**ルール**:
- すべての公開APIはResult型を返す
- 内部実装でのみ例外を使用可
- エラーメッセージは具体的に

### 4. キャッシング戦略の選択

**注意**: 用途に応じて適切な戦略を選択

```kotlin
// オフライン優先（起動高速化）
CachedRepository(local, remote, CacheStrategy.CACHE_FIRST)

// 常に最新（ネットワーク優先）
CachedRepository(local, remote, CacheStrategy.NETWORK_FIRST)

// 完全オフライン
CachedRepository(local, remote, CacheStrategy.CACHE_ONLY)

// キャッシュなし
CachedRepository(local, remote, CacheStrategy.NETWORK_ONLY)
```

**ルール**:
- MVP: CACHE_FIRST（オフライン対応）
- 本番: NETWORK_FIRST（データ鮮度優先）
- デバッグ: NETWORK_ONLY（キャッシュ無効）

### 5. JSON データ形式

**注意**: DTOとドメインモデルを分離

```kotlin
// DTO（JSON用）
@Serializable
data class StarDto(
    val raDegrees: Double,  // JSONは度
    val decDegrees: Double
) {
    fun toDomain(): Star = Star(
        coordinate = EquatorialCoordinate.fromDegrees(raDegrees, decDegrees)
    )
}

// ドメインモデル
data class Star(
    val coordinate: EquatorialCoordinate // 内部はラジアン
)
```

**ルール**:
- JSON層（DTO）は度を使用
- ドメイン層はラジアンを使用
- 変換は必ずDTOで行う

### 6. テストの書き方

**注意**: Pure Functionを活用したテスト

```kotlin
@Test
fun testCoordinateTransform() {
    // Given
    val coord = EquatorialCoordinate.fromDegrees(100.0, 45.0)
    val observer = Observer.fromDegrees(35.0, 139.0)
    val lst = Math.toRadians(180.0)

    // When
    val horizontal = CoordinateTransform.equatorialToHorizontal(coord, observer, lst)

    // Then
    assertTrue(horizontal.altitude >= -Math.PI / 2)
    assertTrue(horizontal.altitude <= Math.PI / 2)
}
```

**ルール**:
- すべての天文計算はPure Function → テスト容易
- 既知の天文値で検証
- Stellariumとの比較テストを追加

### 7. 依存関係の注入

**注意**: コンストラクタインジェクションを使用

```kotlin
// ✅ 正しい例
class GetVisibleStarsUseCase(
    private val repository: StarRepository,
    private val engine: AstronomyEngine
) {
    // 依存関係が明確
}

// 使用側
val useCase = GetVisibleStarsUseCase(
    repository = CachedStarRepository(...),
    engine = AstronomyEngine()
)

// ❌ 間違った例
class GetVisibleStarsUseCase {
    private val repository = LocalStarRepository(...) // ハードコード
}
```

**ルール**:
- すべての依存関係はコンストラクタで受け取る
- シングルトンは避ける
- テスト時にモック可能にする

## 📝 コーディング規約

### 命名規則

- **クラス**: PascalCase (`AstronomyEngine`)
- **関数**: camelCase (`calculatePosition`)
- **定数**: UPPER_SNAKE_CASE (`DEFAULT_TIMEOUT`)
- **プロパティ**: camelCase (`observerLocation`)

### ファイル構成

- 1ファイル = 1クラス（ただし、密接に関連する小クラスは同居可）
- ファイル名 = クラス名
- パッケージは機能別（レイヤー別）

### ドキュメント

```kotlin
/**
 * 天文計算エンジンのメインクラス
 * (Main astronomy calculation engine)
 *
 * 星の位置計算を統合的に行うファサードクラス。
 * 歳差補正、大気差補正などを設定可能。
 *
 * @property config エンジン設定
 */
class AstronomyEngine(
    private val config: EngineConfig = EngineConfig()
) {
    /**
     * 星の位置を計算
     *
     * @param star 対象の星
     * @param observer 観測者位置
     * @param time 観測時刻
     * @return 計算された位置情報
     */
    fun calculateStarPosition(
        star: Star,
        observer: Observer,
        time: Instant
    ): StarPosition
}
```

**ルール**:
- 公開APIには必ずKDocを記述
- 日本語と英語を併記
- パラメータ、戻り値を明記

## 🧪 テスト戦略

### ユニットテスト（commonTest）

- 天文計算の検証
- ドメインモデルのバリデーション
- Pure Functionのテスト

### 統合テスト（TODO）

- Repository層のテスト
- UseCase層のテスト
- エンドツーエンドのデータフロー

### テストカバレッジ目標

- `astronomy/`: 90%以上（重要な計算ロジック）
- `domain/`: 80%以上
- `usecase/`: 80%以上
- `repository/`, `data/`, `api/`: 60%以上

## 🔧 依存関係

### 主要ライブラリ

- **Kotlin Coroutines** 1.8.0 - 非同期処理
- **kotlinx.serialization** 1.6.3 - JSON シリアライゼーション
- **kotlinx.datetime** 0.5.0 - 日時処理
- **Ktor Client** 2.3.8 - HTTP通信

### プラットフォーム固有

- **iOS**: Darwin engine (URLSession)
- **Android**: Android engine (OkHttp)

## 🚀 ビルド方法

```bash
# すべてのターゲットをビルド
./gradlew :shared:build

# テスト実行
./gradlew :shared:test

# iOSフレームワーク生成
./gradlew :shared:embedAndSignAppleFrameworkForXcode
```

## 📚 参考資料

- [Kotlin Multiplatform 公式ドキュメント](https://kotlinlang.org/docs/multiplatform.html)
- [Ktor Client ドキュメント](https://ktor.io/docs/client.html)
- Stellarium - 天文計算の参照実装
- Meeus, "Astronomical Algorithms" - 天文計算の理論

## 🎯 今後の拡張予定

1. **惑星計算** - 太陽系惑星の位置計算
2. **月の位相** - 月の満ち欠け計算
3. **日の出/日の入り** - 天体の出没時刻計算
4. **星雲・星団データ** - メシエ天体カタログ
5. **章動・光行差** - より高精度な補正

---

**Note**: このモジュールは天文学的正確性とユーザー体験の両立を目指して設計されています。
