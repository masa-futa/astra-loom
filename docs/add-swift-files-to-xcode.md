# SwiftファイルをXcodeプロジェクトに追加

## Phase 1実装で作成したファイル

以下のファイルを作成しました：

1. **ViewModels/StarViewModel.swift** - 星の表示用ビューモデル
2. **ViewModels/SkyViewModel.swift** - 星空全体のビューモデル
3. **Views/SkyCanvasView.swift** - 星を描画するCanvasビュー
4. **ContentView.swift** - 更新済み（星空表示に対応）

## Xcodeでの追加手順

### 方法1: Xcodeで直接追加（推奨）

1. **Xcodeでプロジェクトを開く**
   ```
   open /Users/masakifutami/Documents/00_開発/astra-loom/apps/ios/astra-loom/astra-loom.xcodeproj
   ```

2. **プロジェクトナビゲーターで `astra-loom` フォルダを右クリック**

3. **"Add Files to "astra-loom"..." を選択**

4. **以下のフォルダを選択**
   - `ViewModels` フォルダ（中のファイル2つも含む）
   - `Views` フォルダ（中のファイル1つも含む）

5. **オプションを設定**
   - ✅ `Copy items if needed` - **チェックしない**（既にプロジェクト内にあるため）
   - ✅ `Create groups` を選択
   - ✅ `astra-loom` target にチェック

6. **"Add" をクリック**

### 方法2: Finderからドラッグ&ドロップ

1. **Finderで以下のフォルダを開く**
   ```
   /Users/masakifutami/Documents/00_開発/astra-loom/apps/ios/astra-loom/astra-loom/
   ```

2. **Xcodeのプロジェクトナビゲーターを開く**

3. **Finderから `ViewModels` と `Views` フォルダをドラッグ**
   - Xcodeの `astra-loom` グループにドロップ

4. **ダイアログで設定**
   - ✅ `Create groups` を選択
   - ✅ `astra-loom` target にチェック
   - ❌ `Copy items if needed` はチェックしない

## ビルドして実行

ファイル追加後：

1. **クリーンビルド**
   - `Product` > `Clean Build Folder` (⌘⇧K)

2. **ビルド**
   - `Product` > `Build` (⌘B)

3. **実行**
   - `Product` > `Run` (⌘R)
   - または再生ボタンをクリック

## 期待される動作

アプリを起動すると：

- ✨ 美しいグラデーション背景（深夜の空）
- 🌟 100個の明るい星が表示される
- 💫 星の色がスペクトル型に応じて変化
- ✨ 明るい星には輝きエフェクト
- 📱 画面上部に「Astra Loom」と星の数
- 📍 画面下部に観測地点と時刻

## トラブルシューティング

### エラー: "No such module 'shared'"

**原因**: KMPフレームワークが正しく埋め込まれていない

**解決方法**:
```bash
cd /Users/masakifutami/Documents/00_開発/astra-loom
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
```

その後、Xcodeで:
1. `Product` > `Clean Build Folder` (⌘⇧K)
2. `Product` > `Build` (⌘B)

### エラー: "Cannot find type 'StarViewModel' in scope"

**原因**: ファイルがターゲットに追加されていない

**解決方法**:
1. プロジェクトナビゲーターで各`.swift`ファイルを選択
2. 右側のFile Inspectorで"Target Membership"を確認
3. `astra-loom`にチェックが入っているか確認

### 星が表示されない

**原因**: 画面サイズが取得されていない、またはデータ読み込みエラー

**確認方法**:
- Xcodeのコンソールでエラーメッセージを確認
- "星空を読み込み中..."が表示されたままの場合、データ読み込みに失敗している

---

**作成日**: 2026-05-07
**Phase**: 1 - 基本実装
