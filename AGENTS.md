# AGENTS.md

## プロジェクト概要

Tierline MyBatis Addons は、MyBatis で Java の `Optional` 型を扱うためのカスタム TypeHandler を提供するライブラリです。

- **言語**: Java 25
- **ビルドツール**: Gradle 8.11.1 (Gradle Wrapper 使用)
- **主な機能**:
  - `OptionalTypeHandler`: 汎用的な Optional 型のハンドリング
  - `OptionalTimestampTypeHandler`: Optional<OffsetDateTime> のハンドリング

## セットアップコマンド

```bash
# プロジェクトをビルド
./gradlew build

# ローカル Maven リポジトリにインストール
./gradlew lib:publishToMavenLocal

# テストを実行
./gradlew test

# すべての品質チェックを実行（Checkstyle, SpotBugs, PMD, テスト）
./gradlew check

# コードを自動フォーマット
./gradlew spotlessApply

# フォーマットをチェック
./gradlew spotlessCheck
```

## コーディングスタイル

このプロジェクトは **Google Java Style Guide** に準拠しています。

### フォーマット

- Palantir Java Format（Google スタイル）を使用
- インデントはスペース 2 個（タブは使用しない）
- 最大行長: 100 文字
- コミット前に必ず `./gradlew spotlessApply` を実行

### 命名規則

- クラス名: UpperCamelCase
- メソッド名: lowerCamelCase（最低 2 文字以上）
- 変数名: lowerCamelCase
- パッケージ名: 小文字のみ

### Java の機能

- Java 21 の機能を使用（switch 式でのパターンマッチングなど）
- `OptionalTypeHandler.java` でパターンマッチングの例を参照

### ドキュメント

- public および protected メソッドには Javadoc が必須
- すべての public クラスにクラスレベルの Javadoc を記述
- コード内の日本語コメントも可（現在のコードベースで使用中）

## テストについて

### テストの実行

```bash
# すべてのテストを実行
./gradlew test

# 特定のテストクラスを実行
./gradlew test --tests "YourTestClass"

# JaCoCo カバレッジレポートを生成
./gradlew jacocoTestReport
# レポートは lib/build/reports/jacoco/test/html/index.html に生成されます
```

### テストの要件

- JUnit Jupiter 5.10.3 を使用
- テストは `lib/src/test/java/` ディレクトリに追加
- テストはメインコードのパッケージ構造を反映
- 新機能には必ず対応するテストを追加
- コミット前に `./gradlew check` を実行してすべてのテストがパスすることを確認

### テスト作成の方針

#### テストクラスの命名

- テスト対象クラス名 + `Test` (例: `OptionalLocalDateTypeHandlerTest`)

#### @DisplayName の使用

- クラスレベルとメソッドレベルの両方で使用
- 日本語で仕様を明確に記述
- 例:

  ```java
  @DisplayName("OptionalLocalDateTypeHandler のテスト")
  class OptionalLocalDateTypeHandlerTest {
    @Test
    @DisplayName("Optional に値がある場合、PreparedStatement に java.sql.Date として設定される")
    void testSetNonNullParameterWithPresentValue() { ... }
  }
  ```

#### テストコードのスタイル

- Given/When/Then などのコメントは不要（@DisplayName で仕様が明確なため）
- モックは Mockito を使用
- アサーションは JUnit の標準アサーションメソッドを使用

#### モックの設定

- Mockito 5.x を使用
- `mockito-extensions/org.mockito.plugins.MockMaker` で `mock-maker-inline` を有効化
- これにより final クラス（JDBC インターフェースなど）もモック可能

## 品質チェック

すべてのチェックは CI でも実行されます。コミット前にローカルで実行してください。

```bash
# すべてのチェックを一度に実行
./gradlew check

# 個別のチェック
./gradlew checkstyleMain      # Google Java Style 準拠チェック
./gradlew spotbugsMain         # バグの静的解析
./gradlew pmdMain              # コード品質とベストプラクティス
./gradlew spotlessCheck        # フォーマットの検証
```

### 品質チェックツールの設定

- **Checkstyle**: `config/checkstyle/checkstyle.xml`（Google Java Style + 抑制ルール）
- **SpotBugs**: `config/spotbugs/exclude.xml`
- **Spotless**: `lib/build.gradle` で Palantir Java Format を設定

## Pre-commit フック

このプロジェクトは pre-commit を使用しています。

### インストール

```bash
# pre-commit フックをインストール（初回のみ）
pre-commit install
```

### フックの設定

- **常時実行**: ファイルチェック、Markdown lint、Spotless、Checkstyle
- **手動実行のみ**: SpotBugs、PMD（重いチェック）

```bash
# すべてのフックを手動実行
pre-commit run --all-files

# 手動フックの実行
pre-commit run spotbugs --all-files --hook-stage manual
pre-commit run pmd --all-files --hook-stage manual
```

## CI/CD ワークフロー

### GitHub Actions

- **ワークフローファイル**: `.github/workflows/gradle.yml`
- **トリガー**: `main` ブランチへの push と PR
- **実行内容**:
  1. コードのチェックアウト
  2. JDK 25 のセットアップ（Adopt ディストリビューション）
  3. `./gradlew build` の実行（テストと品質チェックを含む）

### プッシュ前のチェックリスト

1. `./gradlew spotlessApply` - コードをフォーマット
2. `./gradlew check` - すべての品質チェックを実行
3. `./gradlew test` - すべてのテストがパスすることを確認
4. 既存の機能を壊していないことを確認

## プロジェクト構造

```text
mybatis-addons/
├── lib/                                    # メインライブラリモジュール
│   ├── src/main/java/
│   │   └── com/tierline/mybatis/typehandler/
│   │       ├── OptionalTypeHandler.java           # 汎用 Optional ハンドラー
│   │       └── OptionalTimestampTypeHandler.java  # Optional<OffsetDateTime> ハンドラー
│   ├── src/test/java/                     # テストディレクトリ（現在空）
│   └── build.gradle                        # モジュールのビルド設定
├── config/                                 # 品質チェックの設定
│   ├── checkstyle/
│   └── spotbugs/
├── gradle/
│   └── libs.versions.toml                  # 依存関係のバージョンカタログ
├── .github/workflows/                      # CI/CD ワークフロー
├── .pre-commit-config.yaml                 # Pre-commit フックの設定
└── settings.gradle                         # マルチプロジェクトの設定
```

## 依存関係

### メインの依存関係（`gradle/libs.versions.toml` 参照）

- **MyBatis**: 3.5.+（最新パッチバージョン）
- **Apache Commons Math3**: 3.6.1
- **Guava**: 33.2.1-jre

### 依存関係の追加

`gradle/libs.versions.toml` を編集して新しい依存関係を追加し、`lib/build.gradle` で参照してください。

## ビルド設定

### Gradle の最適化（`gradle.properties` で有効化）

- Configuration cache
- 並列実行
- ビルドキャッシュ

これらによりビルド時間が大幅に短縮されます。必要な場合を除き無効化しないでください。

## 公開

### ローカル Maven リポジトリへの公開

```bash
./gradlew lib:publishToMavenLocal
```

これにより `~/.m2/repository/` に公開されます:

- **Group ID**: `com.tierline`
- **Artifact ID**: `tierline-mybatis-addons`
- **Version**: `0.0.1`（`lib/build.gradle` で定義）

### ライブラリの使用

Maven Local に公開後、プロジェクトに追加:

```gradle
dependencies {
    implementation 'com.tierline:tierline-mybatis-addons:0.0.1'
}
```

## トラブルシューティング

### ビルドの問題

```bash
# クリーンビルド
./gradlew clean build

# Gradle キャッシュをクリア
rm -rf .gradle build
./gradlew build
```

### フォーマットエラー

```bash
# フォーマットの問題を自動修正
./gradlew spotlessApply
```

### 依存関係の問題

```bash
# 依存関係ツリーを表示
./gradlew lib:dependencies

# 依存関係を更新
./gradlew --refresh-dependencies
```

## タスクワークフロー

このプロジェクトでは `gh` コマンドを使用した標準的なワークフローを推奨しています。

### 1. イシューの作成

```bash
gh issue create \
  --title "イシューのタイトル" \
  --body "イシューの詳細説明" \
  --label "enhancement"
```

### 2. イシューに紐づくブランチの作成

```bash
# イシュー番号を指定してブランチを作成し、チェックアウト
gh issue develop <issue-number> --name "<issue-number>-brief-description" --checkout

# 例: Issue #4 の場合
gh issue develop 4 --name "4-create-agents-md" --checkout
```

これにより、イシューに自動的にリンクされたブランチが作成されます。

### 3. 作業・コミット・プッシュ

```bash
# ファイルを変更
# ...

# 変更をステージング
git add <files>

# コミット（pre-commit フックが自動実行される）
git commit -m "commit message"

# リモートにプッシュ
git push origin <branch-name>
```

### 4. プルリクエストの作成

```bash
gh pr create \
  --title "PR のタイトル" \
  --body "変更内容の説明

Closes #<issue-number>" \
  --base main
```

コミットメッセージやPR本文に `Closes #<issue-number>` を含めることで、PRがマージされた際にイシューが自動的にクローズされます。

### ワークフロー例

```bash
# 1. イシュー作成
gh issue create --title "新機能の追加" --body "詳細説明" --label "enhancement"
# => Issue #5 が作成される

# 2. ブランチ作成
gh issue develop 5 --name "5-add-new-feature" --checkout

# 3. 作業
# コードを編集...
git add .
git commit -m "Add new feature"
git push origin 5-add-new-feature

# 4. PR作成
gh pr create --title "新機能の追加" --body "Closes #5" --base main
```

## 開発のヒント

- グローバルな Gradle インストールではなく、Gradle Wrapper（`./gradlew`）を使用
- プロジェクトには Java 25 が必要 - JAVA_HOME が JDK 25 を指していることを確認
- IDE サポート: IntelliJ IDEA、Eclipse、VS Code（Java Extension Pack が必要）で動作
- Configuration cache によりビルドが高速化されますが、一部のタスクでは `--no-configuration-cache` が必要な場合があります
- `./gradlew tasks` を実行して利用可能なすべてのタスクを確認
- `gh` コマンドを活用してイシューとブランチを効率的に管理
