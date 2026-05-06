# 🌌 星空インタラクティブアプリ 詳細設計書（iPadOS / KMP対応）

## 1. アーキテクチャ概要

本アプリは以下の構成とする：

- UI：SwiftUI（iPadOS）
- ドメインロジック：Kotlin Multiplatform（KMP）
- 天文計算：KMP（Astronomy Engine）
- API Client：KMP（Ktor）
- データ：ローカル＋将来API

---

## 2. システム構成

[ iPadOS (SwiftUI) ]
        ↓
[ ViewModel ]
        ↓
[ KMP Shared Module ]
 ├ domain
 ├ usecase
 ├ astronomy
 ├ repository
 ├ api
 └ model

---

## 3. KMP モジュール設計

### 3.1 Model

data class Star(
    val id: String,
    val name: String?,
    val ra: Double,
    val dec: Double,
    val magnitude: Double
)

data class Constellation(
    val id: String,
    val name: String,
    val starIds: List<String>,
    val lines: List<Pair<String, String>>
)

---

### 3.2 Astronomy Engine

interface AstronomyEngine {
    fun calculateStarPosition(
        star: Star,
        observer: Observer,
        time: Instant
    ): HorizontalCoordinate
}

data class Observer(
    val latitude: Double,
    val longitude: Double
)

data class HorizontalCoordinate(
    val altitude: Double,
    val azimuth: Double
)

---

### 3.3 UseCase

class GetVisibleStarsUseCase(
    private val repository: StarRepository,
    private val engine: AstronomyEngine
) {
    suspend fun execute(observer: Observer, time: Instant): List<VisibleStar> {
        val stars = repository.getStars()
        return stars.map {
            val pos = engine.calculateStarPosition(it, observer, time)
            VisibleStar(it, pos)
        }
    }
}

---

### 3.4 Repository

interface StarRepository {
    suspend fun getStars(): List<Star>
}

---

### 3.5 API Client（Ktor）

class ApiClient {
    private val client = HttpClient()

    suspend fun fetchStars(): List<Star> {
        return client.get("stars_endpoint")
    }
}

---

## 4. iPadOS（SwiftUI）設計

### 4.1 ViewModel

@MainActor
class StarViewModel: ObservableObject {
    @Published var stars: [VisibleStarUI] = []

    func load() async {
        // KMP UseCase呼び出し
    }
}

---

### 4.2 星空描画

Canvas { context, size in
    for star in stars {
        let point = convertToScreen(star.position)
        context.fill(Path(ellipseIn: CGRect(x: point.x, y: point.y, width: 2, height: 2)))
    }
}

---

### 4.3 タップ処理

.onTapGesture { location in
    viewModel.selectNearestStar(location)
}

---

## 5. 状態管理

struct StarScreenState {
    var stars: [VisibleStarUI]
    var selectedStar: VisibleStarUI?
    var activeLayers: Set<LayerType>
}

---

## 6. レイヤー設計

enum class LayerType {
    STAR,
    CONSTELLATION,
    GALAXY,
    PLANET
}

---

## 7. データ戦略

### 初期
- ローカルJSON

### 将来
- API同期
- 差分更新

---

## 8. パフォーマンス設計

- 星数制限（LOD）
- キャッシュ
- バックグラウンド計算

---

## 9. Android展開

- UI：Jetpack Compose
- ViewModel：共有可能
- ロジック：KMP流用

---

## 10. キーメッセージ

本設計は

「天文計算の正確性」と  
「体験としての星空」

を両立するための構成である
