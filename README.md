# Tierline MyBatis Addons

以下のコマンドを実行することでローカルの Maven リポジトリに jar ファイルをインストールすることができる。

```bash
./gradlew lib:publishToMavenLocal
```

## PostgreSQL 統合テスト

### 概要

PostgreSQL を使用した TypeHandler の統合テストです。

### 前提条件

Docker と Docker Compose がインストールされていること。

### 使い方

#### 1. PostgreSQL を起動

```bash
docker compose up -d

# 起動確認
docker compose ps
```

#### 2. テストを実行

```bash
# すべてのテスト（ユニットテスト + 統合テスト）
./gradlew test

# 統合テストのみ
./gradlew test --tests PostgreSqlIntegrationTest
```

#### 3. PostgreSQL を停止

```bash
docker compose down
```

### 接続情報

- ホスト: `localhost`
- ポート: `65432` (デフォルトの 5432 との競合を避けるため)
- データベース: `testdb`
- ユーザー: `test`
- パスワード: `test`

### 注意事項

- PostgreSQL が起動していない場合、統合テストは自動的にスキップされます
- ユニットテストは PostgreSQL なしで実行できます
- ポート 65432 が使用可能である必要があります

### トラブルシューティング

#### ポート 65432 が既に使用されている

```bash
# 使用中のポートを確認
lsof -i:65432

# 既存のプロセスを停止
```

#### PostgreSQL に接続できない

```bash
# コンテナのログを確認
docker compose logs postgres

# 接続テスト
psql -h localhost -p 65432 -U test -d testdb
```
