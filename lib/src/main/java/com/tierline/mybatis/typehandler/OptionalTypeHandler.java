package com.tierline.mybatis.typehandler;

import java.math.BigDecimal;
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
  @SuppressWarnings("unchecked")
  public final void init(T... t) {
    Class<T> type = (Class<T>) t.getClass().getComponentType();
    this.type = type;
  }

  @Override
  public void setNonNullParameter(
      PreparedStatement ps, int i, Optional<T> parameter, JdbcType jdbcType) throws SQLException {
    // ジェネリクスのままではパターンマッチできないので値を取得
    T value = parameter.orElse(null);
    switch (value) {
      case String s -> ps.setString(i, s);
      case Integer intVal -> ps.setInt(i, intVal);
      case Boolean boolVal -> ps.setBoolean(i, boolVal);
      case BigDecimal bd -> ps.setBigDecimal(i, bd);
      case OffsetDateTime odt -> ps.setTimestamp(i, Timestamp.from(odt.toInstant()));
      case Long longVal -> ps.setLong(i, longVal);
      case Double doubleVal -> ps.setDouble(i, doubleVal);
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
      case JdbcType.DECIMAL -> ps.setNull(i, Types.DECIMAL);
      case JdbcType.DOUBLE -> ps.setNull(i, Types.DOUBLE);
      case JdbcType.INTEGER -> ps.setNull(i, Types.INTEGER);
      case JdbcType.NUMERIC -> ps.setNull(i, Types.NUMERIC);
      case JdbcType.OTHER -> ps.setNull(i, Types.OTHER);
      case JdbcType.REAL -> ps.setNull(i, Types.REAL);
      case JdbcType.SMALLINT -> ps.setNull(i, Types.SMALLINT);
      case JdbcType.TIMESTAMP -> ps.setNull(i, Types.TIMESTAMP);
      case JdbcType.TIMESTAMP_WITH_TIMEZONE -> ps.setNull(i, Types.TIMESTAMP_WITH_TIMEZONE);
      case JdbcType.VARCHAR -> ps.setNull(i, Types.VARCHAR);
      default -> throw new UnsupportedOperationException("Unsupported JDBC type: " + jdbcType);
    }
  }

  @Override
  public Optional<T> getNullableResult(ResultSet rs, String columnName) throws SQLException {
    Object value = rs.getObject(columnName);
    if (value == null || rs.wasNull()) {
      return Optional.empty();
    }
    return Optional.of(this.type.cast(value));
  }

  @Override
  public Optional<T> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    Object value = rs.getObject(columnIndex);
    if (value == null || rs.wasNull()) {
      return Optional.empty();
    }
    return Optional.of(this.type.cast(value));
  }

  @Override
  public Optional<T> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    Object value = cs.getObject(columnIndex);
    if (value == null || cs.wasNull()) {
      return Optional.empty();
    }
    return Optional.of(this.type.cast(value));
  }
}
