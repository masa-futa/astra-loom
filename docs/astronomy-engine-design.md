# 🌌 Astronomy Engine 詳細設計（数式ベース / KMP対応）

## 1. 概要

本ドキュメントは、星空インタラクティブアプリにおける天文計算エンジン（Astronomy Engine）の詳細設計を定義する。

対象精度：
- STANDARD（実用レベル + UX最適）
- 歳差補正あり
- 恒星時あり
- 地平座標変換あり
- 大気差補正（簡易）

---

## 2. 計算フロー

J2000座標（RA/Dec）
→ 歳差補正
→ 恒星時（LST）
→ 時角（HA）
→ 地平座標変換（Alt/Az）
→ 大気差補正

---

## 3. データ構造

```kotlin
data class Star(
    val ra: Double,   // radians
    val dec: Double   // radians
)

data class Observer(
    val lat: Double,  // radians
    val lon: Double   // radians
)

data class Time(
    val jd: Double
)
```

---

## 4. ユリウス日（JD）

```text
JD = 367Y 
     - floor(7(Y + floor((M+9)/12)) / 4)
     + floor(275M/9)
     + D + 1721013.5
     + (UT / 24)
```

---

## 5. 世紀数 T

```text
T = (JD - 2451545.0) / 36525
```

---

## 6. 歳差補正（Precession）

IAU 1976モデル（簡易）

```text
ζ = (2306.2181*T + 0.30188*T² + 0.017998*T³) / 3600
z = (2306.2181*T + 1.09468*T² + 0.018203*T³) / 3600
θ = (2004.3109*T - 0.42665*T² - 0.041833*T³) / 3600
```

※角度は度 → ラジアンに変換

---

## 7. 恒星時（GMST）

```text
GMST = 280.46061837 
     + 360.98564736629 * (JD - 2451545.0)
     + 0.000387933*T² 
     - (T³ / 38710000)
```

---

## 8. 地方恒星時（LST）

```text
LST = GMST + longitude
```

---

## 9. 時角（HA）

```text
HA = LST - RA
```

---

## 10. 地平座標変換

```text
sin(alt) = sin(dec)*sin(lat) + cos(dec)*cos(lat)*cos(HA)

cos(A) = (sin(dec) - sin(alt)*sin(lat)) / (cos(alt)*cos(lat))
```

---

## 11. 方位角（Azimuth）

```text
Az = arccos(cos(A))

if sin(HA) > 0:
    Az = 360° - Az
```

---

## 12. 大気差補正（簡易）

```text
R = 1.02 / tan(alt + 10.3/(alt+5.11))
```

---

## 13. KMP実装方針

- すべてDoubleで統一
- ラジアンベース
- 計算はpure function化
- キャッシュ可能な構造

---

## 14. 精度レベル

| レベル | 内容 |
|------|------|
| BASIC | 変換のみ |
| STANDARD | 歳差 + 恒星時 |
| PRECISE | 章動・光行差追加 |

---

## 15. テスト戦略

- Stellariumとの比較
- 同時刻・同位置での差分確認
- 許容誤差：±0.5°以内

---

## 16. キーメッセージ

本エンジンは

「天文学的正確性」と  
「ユーザー体験の自然さ」

の両立を目的とする
