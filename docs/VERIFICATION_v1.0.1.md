# フラッシュ思考 v1.0.1 検証記録

## 1. 対象と方法

- 対象: `app/src/main/res/drawable-nodpi/card_*.png`（問題10項目×3枚=30枚）と `画像/generated/**`（全498枚）
- 推論モデル: `LiquidAI/LFM3-VL-2B-MLX-4bit`（HF、2026年11月9日時点の現行版）を、MLXBar 同捆の mlx-vlm 0.7.12 環境で実行
- プロンプト: 「この画像に写っている主な物や生き物の名前を日本語で1つだけ答えてください。説明や記号は付けず、物の名前だけを短く答えてください。」
- パラメータ: `temperature=0.1, top_p=1.0, min_p=0.0, repetition_penalty=1.1, max_tokens=48`（公式モデルの推奨値）
- 分類結果は発生の検証では保存せず、判定は `/Users/yuki/適当フォルダ/flash-thought-verify/results.json` に保存
- LFM で疑義が生じた画像は、筆者（Claude 4）による目視でも全て確認済み

## 2. 確定した不整合（計6枚・3ペア、全て完全なペアスワップ）

| # | 問題 | 不正な画像 | パートナー画像 | 修正 |
|---|---|---|---|---|
| 1 | 鉛筆 | `鉛筆_1.png`（バレーボール写真） | `バレーボール_3.png`（鉛筆写真） | ファイル内容を交換 |
| 2 | バレーボール | 上記パートナー | 上記 | 同上 |
| 3 | リモコン | `リモコン_1.png`（色鉛筆写真） | `色鉛筆_2.png`（リモコン写真） | ファイル内容を交換 |
| 4 | 色鉛筆 | 上記パートナー | 上記 | 同上 |
| 5 | 段ボール（画像バンク・未採用） | `追加項目/065_段ボール_3.png`（カメラ写真） | `追加項目/066_カメラ（一眼ミラーレス）_2.png`（段ボール写真） | ファイル内容を交換 |
| 6 | カメラ（画像バンク・未採用） | 上記パートナー | 上記 | 同上 |

アプリの `drawable-nodpi/card_02_3.png`, `card_03_1.png`, `card_05_2.png`, `card_06_1.png` も上記内容で差し替え済み。スワップ前の各ハッシュとスワップ後の対応を確認済み（ペアワイズ差分・ハッシュ突合）。

## 3. 修正後検証

- 修正した10ファイル（バンク6 + アプリ4）を LFM3-VL-2B で再判定: 全て問題名と一致
  - `鉛筆_1.png → 鉛筆`、`バレーボール_3.png → ボール`、`リモコン_1.png → リモコン`、`色鉛筆_2.png → 鉛筆`、`段ボール_3 → 箱`、`カメラ_2 → カメラ`
- 修正画像（`バレーボール_3.png`、`色鉛筆_2.png`）を目視で再確認済み
- 改修後の `drawable-nodpi/card_*.png` 全30枚が `画像/generated/<名前>_<番号>.png` とMD5一致

## 4. バンク側のみの既知の問題（アプリの10問には影響なし）

LFM 判定でパラフレーズ（例: 「空調」「階段」）と判別できず、かつペアスワップのパートナーが無いものが複数存在する（例: `追加項目/093_唐傘_1.png` が扇子の写真）。いずれもアプリの現行10問（パプリカ〜洗面器）には含まれないため、将来の追加出題時に再検証が必要。

## 5. アプリ側の検証結果

- `./gradlew assembleDebug` 成功
- `./gradlew assembleRelease`（署名鍵 `memory-flashcards-upload-key.jks`, alias `upload`）成功
  - 署名証明書 SHA-256 = `50460a986439a63b44dcef775bb4f5939e09677741f098b118dbccbac6c445ea` が v1.0.0 と一致
- `versionCode=11`, `versionName=1.0.1`
- aapt2 badging で `application-label-en = "Flash Thought"`、リソースで `card_name_05` の en 値 `Colored pencils` を確認

## 6. 設計評価（安全性・互換性）

| 観点 | 結果 |
|---|---|
| クラッシュ安全性 | `Flashcard.name: String` → `nameRes: Int` への変更を、参照箇所 `FlashcardViewModel`・`FlashcardApp`・`ScoreRepository`・`MainActivity` 全体でgrep確認。`stringResource(card.nameRes)` は CardImageArea 内（コンポーザブル文脈）で呼出し可。`key(card.nameRes)` は安定値（リソースID）で問題なし。 |
| 状態管理 | `rememberSaveable(state.currentIndex)` のキー不変。回転・フォントサイズ変更でも既存動作を維持。 |
| 互換性 | `values` / `values-en` の訳キー数33/33で完全一致。`%1$d/%2$d` フォーマット一致を確認。 |
| 翻訳品質 | `answer_again` の EN は「もう一度」／文脈上「Try again」に修正（v1.0.1内の追加） |

