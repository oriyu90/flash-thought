# 開発用 Android 署名鍵

このディレクトリの `memory-flashcards-development.jks` は、privateリポジトリ内で共有する開発用Android署名鍵である。

リリースビルド時は、次の環境変数で鍵の場所・鍵別名・パスワードを指定する。

```sh
FLASHCARD_STORE_FILE=docs/common-rules/memory-flashcards-development.jks
FLASHCARD_STORE_PASSWORD=...
FLASHCARD_KEY_ALIAS=memoryflashcards
FLASHCARD_KEY_PASSWORD=...
./gradlew assembleRelease
```

本鍵は開発用途に限定する。本番配布用の署名鍵は、このリポジトリとは別のシークレット管理基盤で保管する。
