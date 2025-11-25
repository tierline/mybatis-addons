package com.tierline.mybatis.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/** MyBatis type handler for {@link Optional}. */
public class OptionalTypeHandler<T> extends BaseTypeHandler<Optional<T>> {
  private Class<T> type;

  /** コンストラクタ. */
  @SuppressWarnings("unchecked")
  public OptionalTypeHandler() {
    init();
  }

  /** ジェネリクスの型パラメータの型を取得するためのメソッド. */
  public final void init(T... t) {
    @SuppressWarnings("unchecked")
    Class<T> type = (Class<T>) t.getClass().getComponentType();
    this.type = type;
  }

  @Override
  public void setNonNullParameter(
      PreparedStatement ps, int i, Optional<T> parameter, JdbcType jdbcType) throws SQLException {
    // ジェネリクスのままではパターンマッチできないので値を取得
    T value = parameter.orElse(null);
    switch (value) {
      case OffsetDateTime odt -> ps.setTimestamp(i, Timestamp.from(odt.toInstant()));
      case String s -> ps.setString(i, s);
      case null -> setNull(ps, i, jdbcType);
      default ->
        throw new UnsupportedOperationException(
            "Unsupported type: " + value.getClass().getName());
    }
  }

  private void setNull(PreparedStatement ps, int i, JdbcType jdbcType) throws SQLException {
    switch (jdbcType) {
      case JdbcType.BIGINT -> ps.setNull(i, Types.BIGINT);
      case JdbcType.BIT -> ps.setNull(i, Types.BIT);
      case JdbcType.CHAR -> ps.setNull(i, Types.CHAR);
      case JdbcType.DATE -> ps.setNull(i, Types.DATE);
      case JdbcType.INTEGER -> ps.setNull(i, Types.INTEGER);
      case JdbcType.NUMERIC -> ps.setNull(i, Types.NUMERIC);
      case JdbcType.OTHER -> ps.setNull(i, Types.OTHER);
      case JdbcType.SMALLINT -> ps.setNull(i, Types.SMALLINT);
      case JdbcType.TIMESTAMP -> ps.setNull(i, Types.TIMESTAMP);
      case JdbcType.TIMESTAMP_WITH_TIMEZONE -> ps.setNull(i, Types.TIMESTAMP_WITH_TIMEZONE);
      case JdbcType.VARCHAR -> ps.setNull(i, Types.VARCHAR);
      default -> throw new UnsupportedOperationException("Unsupported JDBC type: " + jdbcType);
    }
  }

  @Override
  public Optional<T> getNullableResult(ResultSet rs, String columnName) throws SQLException {
    return Optional.of(rs.getObject(columnName, this.type));
  }

  @Override
  public Optional<T> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    return Optional.of(rs.getObject(columnIndex, this.type));
  }

  @Override
  public Optional<T> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    return Optional.of(cs.getObject(columnIndex, this.type));
  }
}
