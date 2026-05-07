# Xcode Build Phase スクリプト追加手順

## 手順

1. **Xcodeでプロジェクトを開く**
   ```
   open /Users/masakifutami/Documents/00_開発/astra-loom/apps/ios/astra-loom/astra-loom.xcodeproj
   ```

2. **プロジェクトナビゲーターでプロジェクトを選択**
   - 左サイドバーで `astra-loom` プロジェクト（青いアイコン）をクリック

3. **Targetを選択**
   - `astra-loom` target を選択

4. **Build Phasesタブを開く**
   - 上部のタブから `Build Phases` をクリック

5. **Run Scriptを追加**
   - 左上の `+` ボタンをクリック
   - `New Run Script Phase` を選択

6. **スクリプトを設定**

   **Name（オプション）:**
   ```
   Build KMP Framework
   ```

   **Shell:**
   ```
   /bin/sh
   ```

   **Script:**
   ```bash
   cd "$SRCROOT/../../.."
   ./gradlew :shared:buildXcodeFramework
   ```

7. **順序を調整（重要）**
   - 追加した "Build KMP Framework" を **ドラッグして**
   - "Dependencies" の後、"Compile Sources" の前に移動

8. **入力ファイルを追加（オプションだが推奨）**
   - Run Scriptセクションを展開
   - `Input Files` に以下を追加:
   ```
   $(SRCROOT)/../../../shared/src
   ```

9. **出力ファイルを追加（オプションだが推奨）**
   - `Output Files` に以下を追加:
   ```
   $(SRCROOT)/../../../shared/build/bin/$(PLATFORM_NAME)/$(CONFIGURATION_BUILD_DIR_SUFFIX)/shared.framework
   ```

## ビルドして確認

1. **クリーンビルド**
   - `Product` → `Clean Build Folder` (⇧⌘K)

2. **ビルド実行**
   - `Product` → `Build` (⌘B)
   - または、Run (⌘R)

3. **確認事項**
   - ビルドログに "✅ Framework copied to:" が表示されること
   - エラーなくビルドが完了すること

## トラブルシューティング

### エラー: "gradlew: command not found"
- スクリプトのパスが間違っている可能性
- `cd "$SRCROOT/../../.."` を確認

### エラー: "Task 'buildXcodeFramework' not found"
- Gradle タスク名を確認
- `./gradlew tasks --all | grep -i framework` で確認

### ビルドが遅い
- Gradleデーモンが初回起動に時間がかかります
- 2回目以降は高速化されます

## 代替スクリプト（簡略版）

もしパスの問題が発生する場合は、以下の絶対パスバージョンを使用:

```bash
cd /Users/masakifutami/Documents/00_開発/astra-loom
./gradlew :shared:buildXcodeFramework
```

---

**作成日**: 2026-05-06
**プロジェクト**: Astra Loom
