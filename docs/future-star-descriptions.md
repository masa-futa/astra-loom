# 星の説明機能 - 将来のフェーズ

## 概要
星の詳細画面に、各星の説明・神話・由来などの情報を追加する機能

## 実装予定: Phase 3 または Phase 4

---

## 追加する情報

### 1. 星の説明（日本語）

有名な星（25-50個程度）に対して、以下の情報を追加：

#### 含める内容
- **基本説明**: 星の特徴、見え方、観測のしやすさ
- **天文学的意義**: 距離、実際のサイズ、特殊な特徴
- **観測情報**: いつどこで見えるか、見つけ方

#### 例：シリウス
```
おおいぬ座のα星で、全天で最も明るい恒星です。地球から8.6光年の距離にあり、
白色の主系列星（A型）です。冬の夜空で南の空に輝く青白い星として観測できます。
実際には連星系で、伴星「シリウスB」という白色矮星を持っています。
```

### 2. 神話・由来

#### 含める内容
- **名前の由来**: ギリシャ語、アラビア語などの語源
- **神話**: ギリシャ神話、中国の星宿などの物語
- **文化的意義**: 航海、暦、古代文明での役割

#### 例：ベテルギウス
```
名前はアラビア語の「ヤド・アル・ジャウザー」（巨人の手）に由来します。
ギリシャ神話ではオリオン座の一部として、巨人オリオンの肩を表しています。
近い将来超新星爆発を起こすと予想されており、天文学的に注目されています。
```

---

## データ構造案

### 拡張するモデル

```kotlin
// Star.kt に追加
data class Star(
    val id: String,
    val name: String?,
    val coordinate: EquatorialCoordinate,
    val magnitude: Double,
    val spectralType: String?,

    // 新規追加
    val description: String? = null,        // 星の説明
    val mythology: String? = null,          // 神話・由来
    val distance: Double? = null,           // 距離（光年）
    val constellation: String? = null       // 所属星座（将来）
)
```

### JSONデータ例

```json
{
  "id": "HIP32349",
  "name": "Sirius",
  "raDegrees": 101.287,
  "decDegrees": -16.716,
  "magnitude": -1.46,
  "spectralType": "A1V",
  "distance": 8.6,
  "description": "おおいぬ座のα星で、全天で最も明るい恒星です...",
  "mythology": "ギリシャ神話では、狩人オリオンの猟犬を表しています..."
}
```

---

## UI実装案

### StarDetailView への追加セクション

```swift
// 星の説明セクション
if let description = star.description {
    VStack(alignment: .leading, spacing: 8) {
        Label("この星について", systemImage: "book.fill")
            .font(.headline)

        Text(description)
            .font(.body)
            .foregroundColor(.primary)
    }
}

// 神話・由来セクション
if let mythology = star.mythology {
    VStack(alignment: .leading, spacing: 8) {
        Label("神話と由来", systemImage: "sparkles")
            .font(.headline)

        Text(mythology)
            .font(.body)
            .foregroundColor(.primary)
    }
}
```

---

## 対象となる星リスト（優先度順）

### 優先度: 高（最も有名な星）
1. シリウス (Sirius) - おおいぬ座α
2. ベテルギウス (Betelgeuse) - オリオン座α
3. リゲル (Rigel) - オリオン座β
4. プロキオン (Procyon) - こいぬ座α
5. ベガ (Vega) - こと座α
6. アルタイル (Altair) - わし座α
7. デネブ (Deneb) - はくちょう座α
8. アークトゥルス (Arcturus) - うしかい座α
9. スピカ (Spica) - おとめ座α
10. アンタレス (Antares) - さそり座α
11. アルデバラン (Aldebaran) - おうし座α
12. ポルックス (Pollux) - ふたご座β
13. レグルス (Regulus) - しし座α
14. カペラ (Capella) - ぎょしゃ座α
15. フォーマルハウト (Fomalhaut) - みなみのうお座α

### 優先度: 中（北斗七星など）
16. ミザール (Mizar) - おおぐま座ζ
17. アリオト (Alioth) - おおぐま座ε
18. ドゥーベ (Dubhe) - おおぐま座α
19. メラク (Merak) - おおぐま座β
20. ポラリス (Polaris) - こぐま座α

### 優先度: 低（その他の明るい星）
- カノープス、アケルナル、アクルックスなど

---

## データソース

### 情報収集元
1. **Wikipedia**: 各星の記事（日本語）
2. **国立天文台**: 天文学的データ
3. **星座の神話**: 書籍・オンライン資料
4. **NASA Astronomy Picture of the Day**: 特殊な星の情報

### 作業の進め方
1. 優先度の高い15個の星から開始
2. 各星について200-300文字程度の説明を作成
3. 神話・由来は100-200文字程度
4. レビュー後、JSONに追加

---

## 実装スケジュール案

### Phase 3
- [ ] データ構造の拡張（Star modelに新フィールド追加）
- [ ] 優先度高の15個の星の説明を作成
- [ ] UIに説明セクションを追加

### Phase 4
- [ ] 残りの星の説明を追加
- [ ] 外部API連携の検討（将来的に）
- [ ] ユーザーが星の情報をシェアできる機能

---

## メモ
- 説明文は専門用語を避け、一般の人にも分かりやすく
- 神話は複数の伝承がある場合、最も有名なものを選択
- 距離や物理的特徴は科学的に正確な情報を使用
