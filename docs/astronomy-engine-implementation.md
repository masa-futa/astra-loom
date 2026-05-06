# 🌌 Astronomy Engine 実装・検証設計書（KMP対応）

## 1. 概要

本ドキュメントは、Astronomy Engineの実装方針および検証戦略を定義する。

目的：
- 高精度な星位置計算の実現
- クロスプラットフォームでの再利用（KMP）
- Stellariumとの視覚的一致

---

## 2. 実装方針

### 2.1 レイヤー構成

- astronomy（数式ロジック）
- domain（モデル）
- usecase（アプリロジック）
- datasource（データ取得）

---

### 2.2 実装ルール

- Pure Functionで実装
- Double型で統一
- ラジアンで計算
- 副作用なし
- テスト可能設計

---

### 2.3 モジュール構成（KMP）

```
shared/
├ astronomy/
│ ├ JulianDate.kt
│ ├ SiderealTime.kt
│ ├ Precession.kt
│ ├ CoordinateTransform.kt
│ └ Refraction.kt
├ domain/
├ usecase/
└ repository/
```


---

## 3. コア実装

### 3.1 ユリウス日

```kotlin
fun calculateJulianDate(year: Int, month: Int, day: Int, hour: Double): Double {
    val y = if (month <= 2) year - 1 else year
    val m = if (month <= 2) month + 12 else month

    val a = floor(y / 100.0)
    val b = 2 - a + floor(a / 4)

    return floor(365.25 * (y + 4716)) +
           floor(30.6001 * (m + 1)) +
           day + b - 1524.5 + hour / 24.0
}
```

### 3.2 恒星時（GMST）

```kotlin
fun calculateGMST(jd: Double): Double {
    val T = (jd - 2451545.0) / 36525.0

    return 280.46061837 +
           360.98564736629 * (jd - 2451545.0) +
           0.000387933 * T * T -
           (T * T * T) / 38710000.0
}
```


### 3.3 地方恒星時（LST）

```kotlin
fun calculateLST(gmst: Double, longitude: Double): Double {
    return gmst + longitude
}
```

### 3.4 地平座標変換

```kotlin
fun toHorizontal(
    ra: Double,
    dec: Double,
    lat: Double,
    lst: Double
): Pair<Double, Double> {

    val ha = lst - ra

    val sinAlt = sin(dec) * sin(lat) +
                 cos(dec) * cos(lat) * cos(ha)

    val alt = asin(sinAlt)

    val cosAz = (sin(dec) - sin(alt) * sin(lat)) /
                (cos(alt) * cos(lat))

    var az = acos(cosAz)

    if (sin(ha) > 0) {
        az = 2 * PI - az
    }

    return Pair(alt, az)
}
```

### 3.5 大気差補正

```kotlin
fun applyRefraction(alt: Double): Double {
    val altDeg = Math.toDegrees(alt)
    val correction = 1.02 / tan(Math.toRadians(altDeg + 10.3 / (altDeg + 5.11)))
    return alt + Math.toRadians(correction / 60.0)
}
```

## 4. パフォーマンス設計

- 計算結果キャッシュ（秒単位）
- 可視範囲のみ計算
- バックグラウンド処理
- LOD（星数制御）

## 5. 検証設計
### 5.1 基本方針

- Stellarium との比較
- 許容誤差：±0.5°

### 5.2 テストケース

| ID  | 内容   |
| --- | ---- |
| TC1 | 赤道付近 |
| TC2 | 高緯度  |
| TC3 | 日付差  |
| TC4 | 方角一致 |

### 5.3 検証手順

1. 観測地点を統一
2. 日時を統一
3. Stellariumで確認
4. アプリと比較
5. 差分ログ出力

### 5.4 検証ログ

```kotlin
data class ValidationResult(
    val starId: String,
    val expectedAlt: Double,
    val actualAlt: Double,
    val diff: Double
)
```

## 6. 自動テスト

### Unit Test

- ユリウス日
- 恒星時
- 座標変換

### Integration Test

- 星位置一貫性

## 7. 将来拡張

- 歳差補正（強化）
- 章動
- 光行差
- 惑星計算

## 8. キーメッセージ

本設計は

「天文学的正確性」と
「ユーザー体験」

を両立するための実装基盤である