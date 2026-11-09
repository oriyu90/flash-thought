# フラッシュ思考

画像を見て物の名前を思い出す、Android向けの10問フラッシュカードアプリです。現在のバージョンは v1.0.1 です。

## 開発環境

- Android Studio Ladybug 以降、または Android SDK Platform 35
- JDK 17 以降（開発時はJDK 21を推奨）

`local.properties` に Android SDK の場所を設定してから、次のコマンドでデバッグAPKを作成します。

```sh
JAVA_HOME=/path/to/jdk ./gradlew assembleDebug
```

APK は `app/build/outputs/apk/debug/app-debug.apk` に出力されます。

設計資料は [仕様書](docs/specification.md)、[実装計画](docs/implementation-plan.md)、[ADR](docs/adr/0001-compose-material3-and-local-storage.md) にあります。
