# Common Rules

## Android 開発用署名

- 開発用署名鍵は `common-rules/memory-flashcards-development.jks` としてprivateリポジトリ内で共有する。
- 鍵別名と署名パスワードは、リポジトリ・文書・チケットへ保存または投稿しない。
- 本番配布用の署名鍵はアクセス制御と監査ログを備えた専用シークレットストアに保管し、必要なリリース担当者だけにアクセスを許可する。
- CI/CD では `FLASHCARD_STORE_FILE`、`FLASHCARD_STORE_PASSWORD`、`FLASHCARD_KEY_ALIAS`、`FLASHCARD_KEY_PASSWORD` をシークレットとして設定する。
- 開発用鍵を紛失すると開発ビルドの継続に支障が出るため、暗号化済みのバックアップを別保管する。
- 本番鍵やパスワードが露出した疑いがある場合は、配布を止め、状況を記録して鍵ローテーションの可否を判断する。

## ローカルでの署名ビルド

署名情報を環境変数で渡して、次を実行する。

```sh
./gradlew assembleRelease
```

出力先は `app/build/outputs/apk/release/app-release.apk`。公開前には、署名証明書のフィンガープリントを記録済みの値と照合する。
