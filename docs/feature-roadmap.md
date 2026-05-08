# Astra Loom - 機能ロードマップ

**最終更新**: 2026-05-08
**バージョン**: 2.0

---

## 📋 現状の課題認識

### 強み
- ✅ 天文学的に正確な星の位置計算
- ✅ リアルタイムな観測シミュレーション
- ✅ インタラクティブなUI/UX

### 弱み・改善点
- ⚠️ **教育的側面が弱い**: 星座を学びたい人には情報不足
- ⚠️ **天体の種類が限定的**: 星座と恒星のみ（惑星、銀河、星雲なし）
- ⚠️ **文化的情報の欠如**: 神話、由来、歴史的背景がない
- ⚠️ **学習機能の不足**: 段階的な学習パスがない

---

## 🎯 目指す方向性

### コンセプト
**「見る」から「学ぶ」へ - インタラクティブな天文学習プラットフォーム**

### ターゲットユーザー
1. **初心者**: 星座を学びたい、天文学に興味を持ち始めた人
2. **中級者**: 惑星や深宇宙天体を観測したい人
3. **教育者**: 天文教育に活用したい教師・講師
4. **天文ファン**: より深い知識と観測体験を求める人

### 3つの柱
1. **正確性** - 天文学的に正確なシミュレーション（維持）
2. **教育性** - 段階的な学習をサポート（強化）
3. **探索性** - 自由な発見と探索の喜び（新規）

---

## 🗺️ 実装フェーズ

## Phase 4: 星座学習機能の強化 🌟

**目標**: 星座を見つけ、理解し、覚えられるようにする

### 4.1 星座線の完全実装

**現状**: 10星座のみ、ほとんど線データなし
**目標**: 主要88星座の線データを整備

#### 実装内容
- [ ] 星座線データの拡充（constellations.jsonの更新）
  - 主要30星座の完全な線データ
  - 黄道12星座（最優先）
  - 四季の代表的星座
- [ ] 星座線の描画機能（SkyCanvasView）
  - 半透明の線で描画
  - 星座ごとに色分け（オプション）
  - 表示/非表示の切り替え
- [ ] 星座名ラベルの表示
  - 星座の中心位置に表示
  - 日本語/英語/学名の切り替え
  - フォントサイズ調整

#### データ構造の拡張

```kotlin
// Constellation.kt に追加
data class Constellation(
    val id: String,                    // 既存
    val name: String,                  // 既存
    val nameJa: String,                // 新規: 日本語名
    val genitive: String,              // 新規: 属格（例: Orionis）
    val abbreviation: String,          // 新規: 略称（例: Ori）
    val starIds: List<String>,         // 既存
    val lines: List<Pair<String, String>>, // 既存
    val area: Double,                  // 新規: 面積（平方度）
    val rank: Int,                     // 新規: 面積ランキング（1-88）
    val season: Season,                // 新規: 観測に適した季節
    val hemisphere: Hemisphere,        // 新規: 主に見える半球
    val mythology: String?,            // 新規: 神話・由来
    val description: String?,          // 新規: 説明
    val findingTips: String?           // 新規: 見つけ方のヒント
)

enum class Season {
    SPRING, SUMMER, AUTUMN, WINTER, ALL_YEAR
}

enum class Hemisphere {
    NORTHERN, SOUTHERN, EQUATORIAL
}
```

#### 優先度付き実装リスト

**優先度: 最高（黄道12星座）**
1. おひつじ座 (Aries) - Ari
2. おうし座 (Taurus) - Tau
3. ふたご座 (Gemini) - Gem
4. かに座 (Cancer) - Cnc
5. しし座 (Leo) - Leo
6. おとめ座 (Virgo) - Vir
7. てんびん座 (Libra) - Lib
8. さそり座 (Scorpius) - Sco
9. いて座 (Sagittarius) - Sgr
10. やぎ座 (Capricornus) - Cap
11. みずがめ座 (Aquarius) - Aqr
12. うお座 (Pisces) - Psc

**優先度: 高（有名な星座）**
13. オリオン座 (Orion) - Ori ✅ 既存
14. おおぐま座 (Ursa Major) - UMa ✅ 既存
15. こぐま座 (Ursa Minor) - UMi ✅ 既存
16. カシオペヤ座 (Cassiopeia) - Cas ✅ 既存
17. はくちょう座 (Cygnus) - Cyg ✅ 既存
18. こと座 (Lyra) - Lyr
19. わし座 (Aquila) - Aql ✅ 既存
20. ペガスス座 (Pegasus) - Peg

**優先度: 中（季節の代表星座）**
- 春: しし座、おとめ座、うしかい座
- 夏: さそり座、いて座、へびつかい座
- 秋: ペガスス座、アンドロメダ座
- 冬: オリオン座、おおいぬ座、こいぬ座

### 4.2 星座学習モード

**目標**: 初心者が段階的に星座を学べる機能

#### 実装内容
- [ ] 星座ガイドモード
  - 選択した星座をハイライト表示
  - 星座線を強調
  - 星座の形を示すアニメーション
- [ ] 星座クイズモード
  - 星座の形から名前を当てる
  - 主要な星から星座を特定
  - 難易度別（初級/中級/上級）
- [ ] 季節別星座表示
  - 春夏秋冬で見える星座をフィルター
  - 今夜見える星座のリスト
  - おすすめ観測時刻の表示
- [ ] 星座図鑑
  - 全88星座のリスト
  - 各星座の詳細情報
  - お気に入り機能

### 4.3 神話と文化的背景

**目標**: 星座の物語を伝える

#### 実装内容
- [ ] 神話ストーリー
  - ギリシャ神話
  - 日本の星の伝承
  - 中国の星宿（二十八宿）
  - 世界各地の星座神話
- [ ] 歴史的背景
  - 星座の起源
  - 天文学史との関連
  - 文化による解釈の違い
- [ ] 音声ナレーション（将来）
  - 神話の朗読
  - 音楽・効果音

---

## Phase 5: 星の詳細情報の拡充 ⭐

**目標**: 有名な星について深く学べるようにする

### 5.1 星の説明・神話

**現状**: 物理的特徴のみ（スペクトル型、等級、座標）
**目標**: 50-100個の主要な星に詳細情報を追加

#### データ構造の拡張

```kotlin
// Star.kt に追加
data class Star(
    val id: String,                    // 既存
    val name: String?,                 // 既存
    val coordinate: EquatorialCoordinate, // 既存
    val magnitude: Double,             // 既存
    val spectralType: String?,         // 既存

    // 新規フィールド
    val properNames: List<String>,     // 固有名（複数言語）
    val nameOrigin: String?,           // 名前の由来
    val nameOriginLang: String?,       // 由来の言語（アラビア語など）
    val distance: Double?,             // 距離（光年）
    val absoluteMagnitude: Double?,    // 絶対等級
    val luminosity: Double?,           // 光度（太陽=1）
    val radius: Double?,               // 半径（太陽=1）
    val mass: Double?,                 // 質量（太陽=1）
    val temperature: Int?,             // 表面温度（K）
    val age: Double?,                  // 年齢（億年）
    val description: String?,          // 説明（日本語）
    val descriptionEn: String?,        // 説明（英語）
    val mythology: String?,            // 神話・伝承
    val culturalSignificance: String?, // 文化的意義
    val observationTips: String?,      // 観測のヒント
    val variableType: String?,         // 変光星の種類
    val companionStars: List<String>,  // 伴星のID
    val isDoubleStar: Boolean,         // 二重星か
    val isBinaryStar: Boolean          // 連星か
)
```

#### 優先度付き実装リスト

**優先度: 最高（1等星）**
1. シリウス (Sirius) - おおいぬ座α - HIP32349
2. カノープス (Canopus) - りゅうこつ座α - HIP30438
3. リギル・ケンタウルス (Rigil Kentaurus) - ケンタウルス座α - HIP71683
4. アークトゥルス (Arcturus) - うしかい座α - HIP69673
5. ベガ (Vega) - こと座α - HIP91262
6. カペラ (Capella) - ぎょしゃ座α - HIP24608
7. リゲル (Rigel) - オリオン座β - HIP24436
8. プロキオン (Procyon) - こいぬ座α - HIP37279
9. ベテルギウス (Betelgeuse) - オリオン座α - HIP27989
10. アケルナル (Achernar) - エリダヌス座α - HIP7588

**優先度: 高（2等星＋特徴的な星）**
11. アルデバラン (Aldebaran) - おうし座α - 赤色巨星
12. アンタレス (Antares) - さそり座α - 赤色超巨星
13. スピカ (Spica) - おとめ座α - 青白い星
14. デネブ (Deneb) - はくちょう座α - 超巨星
15. アルタイル (Altair) - わし座α - 夏の大三角
16. ポラリス (Polaris) - こぐま座α - 北極星
17. フォーマルハウト (Fomalhaut) - みなみのうお座α
18. ポルックス (Pollux) - ふたご座β - 巨星
19. レグルス (Regulus) - しし座α
20. ミザール (Mizar) - おおぐま座ζ - 二重星

**優先度: 中（重要な星）**
- カストル (Castor) - ふたご座α - 6重星系
- アルビレオ (Albireo) - はくちょう座β - 美しい二重星
- ミラ (Mira) - くじら座ο - 変光星
- アルゴル (Algol) - ペルセウス座β - 食変光星

### 5.2 星の説明コンテンツ

各星について以下の情報を日本語で作成：

#### テンプレート
```markdown
## [星の名前]

### 基本情報
- **名前**: [固有名] ([学名])
- **星座**: [所属星座]
- **等級**: [視等級]
- **距離**: [光年]
- **スペクトル型**: [分類]

### 名前の由来
[語源、意味、歴史的背景]

### 特徴
[天文学的特徴、興味深い点]

### 神話・伝承
[ギリシャ神話、日本の伝承、その他]

### 観測ガイド
[見つけ方、観測に適した時期、肉眼で見えるか]
```

#### コンテンツ例：シリウス

```markdown
## シリウス (Sirius)

### 基本情報
- **名前**: シリウス (Sirius / おおいぬ座α星)
- **星座**: おおいぬ座
- **等級**: -1.46等（全天で最も明るい恒星）
- **距離**: 8.6光年
- **スペクトル型**: A1V（白色主系列星）

### 名前の由来
ギリシャ語の「セイリオス」（σείριος、「焼き焦がすもの」「光り輝くもの」）に由来。
夏の暑い時期に太陽とともに昇ることから、この名が付けられました。

古代エジプトでは「ソプデト」と呼ばれ、ナイル川の氾濫期の到来を告げる星として
重要視されていました。

日本では「青星（あおぼし）」「大星（おおぼし）」などと呼ばれました。

### 特徴
- 太陽の約2倍の質量と25倍の光度を持つ白色の主系列星
- 連星系で、伴星「シリウスB」は白色矮星（発見は1862年）
- 地球から8.6光年と比較的近く、固有運動も大きい
- 冬の夜空で最も目立つ星で、青白く輝いて見える
- シリウスBは初めて発見された白色矮星として天文学史上重要

### 神話・伝承
ギリシャ神話では、猟師オリオンの猟犬を表す星とされています。
オリオンが蠍に刺されて死んだ後も、忠実な犬として主人に寄り添い続けているとされます。

古代エジプトでは、シリウスの出現が新年とナイル川の氾濫期の始まりを示す
重要な暦の基準でした。

### 観測ガイド
- **見つけ方**: オリオン座の三ツ星を左下に延長した先
- **観測時期**: 12月〜3月（冬の夜空）
- **肉眼観測**: 非常に明るく、都市部でも容易に見つけられる
- **双眼鏡**: 青白い輝きがより鮮明に
- **望遠鏡**: 伴星シリウスBの観測は難しい（本星が明るすぎるため）
```

---

## Phase 6: 太陽系天体の実装 🪐

**目標**: 惑星を観測し、太陽系を理解する

### 6.1 惑星位置計算エンジン

#### 実装内容

**ドメインモデル**
```kotlin
// domain/Planet.kt
data class Planet(
    val id: String,              // "Mercury", "Venus", etc.
    val name: String,            // "水星", "金星", etc.
    val nameEn: String,          // 英語名
    val order: Int,              // 太陽からの順番（1-8）
    val type: PlanetType,        // TERRESTRIAL, GAS_GIANT, ICE_GIANT
    val mass: Double,            // 質量（地球=1）
    val radius: Double,          // 半径（地球=1）
    val orbitalPeriod: Double,   // 公転周期（日）
    val rotationPeriod: Double,  // 自転周期（時間）
    val distanceFromSun: Double, // 平均距離（AU）
    val description: String,     // 説明
    val mythology: String        // 神話・由来
)

enum class PlanetType {
    TERRESTRIAL,  // 地球型惑星（岩石惑星）
    GAS_GIANT,    // ガス惑星
    ICE_GIANT     // 氷惑星
}
```

**天文計算**
```kotlin
// astronomy/PlanetCalculator.kt
class PlanetCalculator {
    /**
     * VSOP87理論による惑星位置計算
     * (簡易版：6要素の軌道計算)
     */
    fun calculatePlanetPosition(
        planet: Planet,
        observer: Observer,
        time: Instant
    ): PlanetPosition

    /**
     * 惑星の視等級計算
     */
    fun calculateApparentMagnitude(
        planet: Planet,
        distanceFromEarth: Double,
        phaseAngle: Double
    ): Double

    /**
     * 惑星の位相計算（内惑星）
     */
    fun calculatePhase(
        planet: Planet,
        time: Instant
    ): Double  // 0.0-1.0 (新-満)
}
```

### 6.2 惑星データ

#### 実装する惑星
1. **水星** (Mercury) - 最も内側、観測が難しい
2. **金星** (Venus) - 明けの明星・宵の明星
3. **火星** (Mars) - 赤い惑星
4. **木星** (Jupiter) - 最大の惑星、ガリレオ衛星
5. **土星** (Saturn) - 環を持つ惑星
6. **天王星** (Uranus) - 横倒しの惑星
7. **海王星** (Neptune) - 最も外側のガス惑星
8. **（準惑星）冥王星** (Pluto) - オプション

#### 各惑星の詳細情報

**金星の例**
```json
{
  "id": "Venus",
  "name": "金星",
  "nameEn": "Venus",
  "order": 2,
  "type": "TERRESTRIAL",
  "mass": 0.815,
  "radius": 0.949,
  "orbitalPeriod": 224.7,
  "rotationPeriod": 5832.5,
  "distanceFromSun": 0.723,
  "description": "地球の姉妹惑星と呼ばれる金星は、太陽系で最も高温の惑星です。厚い二酸化炭素の大気により、強力な温室効果が生じ、表面温度は約460℃に達します。明け方や夕方に見える明るい星で、古くから「明けの明星」「宵の明星」として親しまれてきました。",
  "mythology": "ローマ神話の美と愛の女神ヴィーナス（Venus）に由来します。ギリシャ神話のアフロディーテに相当します。明るく美しく輝くことから、この名前が付けられました。",
  "observationTips": "日の出前の東の空、または日没後の西の空に見えます。非常に明るく（-4等級）、都市部でも容易に観測できます。望遠鏡では月のような満ち欠けが観測できます。",
  "satellites": [],
  "rings": false
}
```

### 6.3 惑星観測機能

#### UI実装
- [ ] 惑星の表示
  - 星空上に惑星を表示（記号または名前ラベル）
  - 視等級に応じたサイズ
  - 惑星の色（火星=赤、木星=オレンジなど）
- [ ] 惑星詳細パネル
  - 現在の位置（高度、方位）
  - 視等級、距離
  - 次の最接近日時
  - 観測に適した時期
- [ ] 惑星カレンダー
  - 惑星の動き（逆行など）
  - 衝、合、最大離角の日付
  - 惑星同士の接近
- [ ] 太陽系ビューアー（3D表示）
  - 太陽系全体の俯瞰図
  - 惑星の軌道
  - 現在位置の表示

### 6.4 月の実装

#### 実装内容
- [ ] 月の位置計算（高精度）
- [ ] 月齢計算
- [ ] 月の満ち欠けビジュアライゼーション
- [ ] 月の詳細情報
  - クレーター、海の名前
  - 月面地図
- [ ] 月食計算と表示

---

## Phase 7: 深宇宙天体の実装 🌌

**目標**: 銀河、星雲、星団を観測し、宇宙の広がりを感じる

### 7.1 対象天体

#### メシエ天体（優先度：高）
全110天体のうち、主要な50天体を実装

**カテゴリ別**
- **銀河** (Galaxies): M31アンドロメダ銀河、M51子持ち銀河など
- **星雲** (Nebulae): M42オリオン大星雲、M8干潟星雲など
- **星団** (Clusters):
  - 球状星団: M13ヘルクレス座球状星団など
  - 散開星団: M45プレアデス星団（すばる）など

#### NGC天体（優先度：中）
- NGC2000など、有名な天体を厳選

### 7.2 深宇宙天体データモデル

```kotlin
// domain/DeepSkyObject.kt
data class DeepSkyObject(
    val id: String,              // "M31", "NGC224"
    val names: List<String>,     // ["アンドロメダ銀河", "Andromeda Galaxy"]
    val type: DSOType,
    val coordinate: EquatorialCoordinate,
    val magnitude: Double,       // 視等級
    val size: Double,            // 視直径（分）
    val constellation: String,   // 所属星座
    val distance: Double,        // 距離（光年 or Mpc）
    val description: String,     // 説明
    val observationDifficulty: Difficulty,
    val imageUrl: String?,       // 画像URL
    val discoverer: String?,     // 発見者
    val discoveryYear: Int?      // 発見年
)

enum class DSOType {
    GALAXY,              // 銀河
    NEBULA,              // 星雲
    PLANETARY_NEBULA,    // 惑星状星雲
    GLOBULAR_CLUSTER,    // 球状星団
    OPEN_CLUSTER,        // 散開星団
    SUPERNOVA_REMNANT    // 超新星残骸
}

enum class Difficulty {
    NAKED_EYE,      // 肉眼
    BINOCULARS,     // 双眼鏡
    SMALL_TELESCOPE,// 小型望遠鏡
    LARGE_TELESCOPE // 大型望遠鏡
}
```

### 7.3 優先実装リスト

**優先度: 最高（肉眼・双眼鏡で見える）**
1. M31 - アンドロメダ銀河（銀河）
2. M42 - オリオン大星雲（散光星雲）
3. M45 - プレアデス星団／すばる（散開星団）
4. M44 - プレセペ星団（散開星団）
5. M13 - ヘルクレス座球状星団（球状星団）

**優先度: 高（望遠鏡で美しい）**
6. M51 - 子持ち銀河（銀河）
7. M57 - リング星雲（惑星状星雲）
8. M27 - 亜鈴状星雲（惑星状星雲）
9. M8 - 干潟星雲（散光星雲）
10. M20 - 三裂星雲（散光星雲）

**優先度: 中（有名な天体）**
- M1 - かに星雲（超新星残骸）
- M33 - さんかく座銀河（銀河）
- M104 - ソンブレロ銀河（銀河）

### 7.4 深宇宙天体機能

#### UI実装
- [ ] 深宇宙天体の表示
  - 星空上にアイコン表示
  - 種類別のアイコン（銀河、星雲、星団）
- [ ] フィルター機能
  - 種類でフィルター
  - 観測難易度でフィルター
  - 今夜見える天体のみ表示
- [ ] 詳細パネル
  - 説明、画像
  - 観測方法のアドバイス
  - 適した機材の推奨
- [ ] 深宇宙天体カタログ
  - メシエカタログブラウザ
  - NGC/ICカタログ
  - お気に入り機能

---

## Phase 8: 天文イベント機能 📅

**目標**: 天文現象を予測し、観測を促す

### 8.1 天文イベント種類

#### 定期イベント
- [ ] 流星群
  - ペルセウス座流星群
  - しぶんぎ座流星群
  - ふたご座流星群
- [ ] 日食・月食
- [ ] 惑星の衝・合
- [ ] 惑星の最大離角（水星、金星）

#### 不定期イベント
- [ ] 彗星の接近
- [ ] 小惑星の接近
- [ ] 国際宇宙ステーション（ISS）の可視通過

### 8.2 イベント通知機能

- [ ] イベントカレンダー
- [ ] プッシュ通知
- [ ] 観測ガイド
  - 観測に最適な時刻
  - 観測に適した場所の提案
  - 必要な機材

---

## Phase 9: 学習・教育機能 📚

**目標**: 体系的に天文学を学べるプラットフォーム

### 9.1 学習コンテンツ

#### 初級コース
- [ ] 星座入門（10星座を覚えよう）
- [ ] 1等星を全部見つけよう
- [ ] 惑星を見つけよう
- [ ] 月の満ち欠けを理解しよう

#### 中級コース
- [ ] 季節の星座（春夏秋冬）
- [ ] 二重星を観測しよう
- [ ] メシエ天体マラソン
- [ ] 惑星の動きを追跡しよう

#### 上級コース
- [ ] 座標系を理解しよう（赤道座標、地平座標）
- [ ] 歳差運動を学ぼう
- [ ] 恒星の進化を理解しよう

### 9.2 インタラクティブ機能

- [ ] クイズモード
- [ ] アチーブメント（実績）
  - 「10個の星座を見つけた」
  - 「全惑星を観測した」
  - 「メシエ天体50個制覇」
- [ ] 観測ログ
  - 観測記録をつける
  - 写真を添付
  - SNSシェア
- [ ] コミュニティ機能
  - 観測レポートの共有
  - 質問・回答フォーラム

---

## 🛠️ 技術的実装計画

### KMP共有モジュールの拡張

#### 新規ドメインモデル
```
domain/
├── Star.kt (拡張)
├── Constellation.kt (拡張)
├── Planet.kt (新規)
├── DeepSkyObject.kt (新規)
├── AstronomicalEvent.kt (新規)
└── LearningContent.kt (新規)
```

#### 新規天文計算
```
astronomy/
├── AstronomyEngine.kt (既存)
├── SunCalculator.kt (既存)
├── PlanetCalculator.kt (新規)
├── MoonCalculator.kt (新規)
├── EclipseCalculator.kt (新規)
└── MeteorShowerCalculator.kt (新規)
```

#### データソース
```
data/
├── source/
│   ├── LocalStarDataSource.kt (既存)
│   ├── LocalConstellationDataSource.kt (既存)
│   ├── LocalPlanetDataSource.kt (新規)
│   ├── LocalDeepSkyObjectDataSource.kt (新規)
│   └── LocalEventDataSource.kt (新規)
└── resources/
    ├── stars.json (既存 - 拡張)
    ├── constellations.json (既存 - 拡張)
    ├── planets.json (新規)
    ├── deep_sky_objects.json (新規)
    └── events.json (新規)
```

### iOS実装

#### 新規ビュー
```
Views/
├── ConstellationLinesView.swift (新規)
├── PlanetDetailView.swift (新規)
├── DeepSkyObjectView.swift (新規)
├── EventCalendarView.swift (新規)
├── LearningModeView.swift (新規)
└── QuizView.swift (新規)
```

---

## 📊 実装優先順位マトリクス

| フェーズ | 機能 | 教育的価値 | 技術的複雑度 | ユーザー需要 | 優先度 |
|---------|------|-----------|-------------|-------------|--------|
| Phase 4.1 | 星座線表示 | ⭐⭐⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐⭐⭐ | **最高** |
| Phase 4.2 | 星座学習モード | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ | **最高** |
| Phase 5 | 星の詳細情報 | ⭐⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐⭐ | **高** |
| Phase 6 | 惑星実装 | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | **高** |
| Phase 7 | 深宇宙天体 | ⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐ | **中** |
| Phase 8 | 天文イベント | ⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | **中** |
| Phase 9 | 学習コンテンツ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ | **高** |

---

## 🎯 推奨実装順序

### 短期（1-2ヶ月）
1. **Phase 4.1**: 星座線表示（黄道12星座 + 有名な星座20個）
2. **Phase 5**: 主要な星50個の詳細情報

### 中期（3-4ヶ月）
3. **Phase 4.2**: 星座学習モード
4. **Phase 6.1-6.2**: 惑星実装（8惑星の位置計算とデータ）

### 長期（5-6ヶ月）
5. **Phase 6.3**: 惑星観測機能
6. **Phase 7**: 深宇宙天体（主要50天体）
7. **Phase 9**: 学習コンテンツとクイズ

---

## 📝 データ作成タスク

### 星座データ（constellations.json拡張）
- [ ] 黄道12星座の完全な線データ
- [ ] 各星座の神話・説明（日本語）
- [ ] 季節、半球、面積などのメタデータ

### 星データ（stars.json拡張）
- [ ] 主要50星の詳細情報執筆
- [ ] 名前の由来調査
- [ ] 神話・文化的背景の執筆

### 惑星データ（planets.json新規）
- [ ] 8惑星の物理データ
- [ ] 各惑星の説明・神話
- [ ] 観測ガイド執筆

### 深宇宙天体データ（deep_sky_objects.json新規）
- [ ] メシエ天体50個のデータ
- [ ] 説明文執筆
- [ ] 観測難易度の設定

---

## 🔗 参考資料・データソース

### 天文データ
- **Yale Bright Star Catalog** - 星のデータ
- **Stellarium** - 星座線データ
- **IAU** - 星座の公式情報
- **NASA/JPL Horizons** - 惑星の軌道要素
- **Messier Catalog** - メシエ天体
- **NGC/IC Database** - 深宇宙天体

### 神話・文化的情報
- Wikipedia（日本語/英語）
- 国立天文台の資料
- 「星座の神話」関連書籍
- 各地の星の民間伝承

---

## ✅ 成功指標（KPI）

### ユーザーエンゲージメント
- 月間アクティブユーザー数
- 平均セッション時間
- 学習モード完了率

### 教育効果
- クイズ正答率の向上
- 星座認識数の増加
- 観測ログの記録数

### コンテンツ充実度
- 実装済み星座数 / 88
- 詳細情報のある星の数 / 100
- 実装済み惑星数 / 8
- 実装済み深宇宙天体数 / 50

---

**Note**: このロードマップは、「見る」だけのアプリから「学ぶ」アプリへの進化を目指します。
天文学的正確性を保ちながら、教育的価値と探索の喜びを提供することが目標です。
