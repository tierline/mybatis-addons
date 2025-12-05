package com.tierline.mybatis.typehandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Test class for {@link OptionalTypeHandler}. */
@DisplayName("OptionalTypeHandler のテスト")
class OptionalTypeHandlerTest {

  @Test
  @DisplayName("Optional<String> に値がある場合、PreparedStatement に String として設定される")
  void testSetNonNullParameterWithStringValue() throws SQLException {
    OptionalTypeHandler<String> handler = new OptionalTypeHandler<>();
    PreparedStatement ps = mock(PreparedStatement.class);
    String testValue = "test string";
    Optional<String> parameter = Optional.of(testValue);
    int parameterIndex = 1;

    handler.setNonNullParameter(ps, parameterIndex, parameter, JdbcType.VARCHAR);

    verify(ps).setString(parameterIndex, testValue);
  }

  @Test
  @DisplayName("Optional<OffsetDateTime> に値がある場合、PreparedStatement に Timestamp として設定される")
  void testSetNonNullParameterWithOffsetDateTimeValue() throws SQLException {
    OptionalTypeHandler<OffsetDateTime> handler = new OptionalTypeHandler<>();
    PreparedStatement ps = mock(PreparedStatement.class);
    OffsetDateTime testDateTime =
        OffsetDateTime.of(2024, 1, 15, 10, 30, 0, 0, ZoneOffset.ofHours(9));
    Optional<OffsetDateTime> parameter = Optional.of(testDateTime);
    int parameterIndex = 1;

    handler.setNonNullParameter(ps, parameterIndex, parameter, JdbcType.TIMESTAMP);

    verify(ps).setTimestamp(parameterIndex, Timestamp.from(testDateTime.toInstant()));
  }

  @Test
  @DisplayName("Optional が空の場合、PreparedStatement に NULL（VARCHAR型）が設定される")
  void testSetNonNullParameterWithEmptyValueVarchar() throws SQLException {
    OptionalTypeHandler<String> handler = new OptionalTypeHandler<>();
    PreparedStatement ps = mock(PreparedStatement.class);
    Optional<String> parameter = Optional.empty();
    int parameterIndex = 1;

    handler.setNonNullParameter(ps, parameterIndex, parameter, JdbcType.VARCHAR);

    verify(ps).setNull(parameterIndex, Types.VARCHAR);
  }

  @Test
  @DisplayName("Optional が空の場合、PreparedStatement に NULL（TIMESTAMP型）が設定される")
  void testSetNonNullParameterWithEmptyValueTimestamp() throws SQLException {
    OptionalTypeHandler<OffsetDateTime> handler = new OptionalTypeHandler<>();
    PreparedStatement ps = mock(PreparedStatement.class);
    Optional<OffsetDateTime> parameter = Optional.empty();
    int parameterIndex = 1;

    handler.setNonNullParameter(ps, parameterIndex, parameter, JdbcType.TIMESTAMP);

    verify(ps).setNull(parameterIndex, Types.TIMESTAMP);
  }

  @Test
  @DisplayName("Optional が空の場合、PreparedStatement に NULL（INTEGER型）が設定される")
  void testSetNonNullParameterWithEmptyValueInteger() throws SQLException {
    OptionalTypeHandler<Integer> handler = new OptionalTypeHandler<>();
    PreparedStatement ps = mock(PreparedStatement.class);
    Optional<Integer> parameter = Optional.empty();
    int parameterIndex = 1;

    handler.setNonNullParameter(ps, parameterIndex, parameter, JdbcType.INTEGER);

    verify(ps).setNull(parameterIndex, Types.INTEGER);
  }

  @Test
  @DisplayName("ResultSet からカラム名で String 値を取得すると、Optional<String> に変換される")
  void testGetNullableResultByColumnNameWithString() throws SQLException {
    OptionalTypeHandler<String> handler = new OptionalTypeHandler<>();
    handler.init(new String[0]);
    ResultSet rs = mock(ResultSet.class);
    String columnName = "test_column";
    String testValue = "test string";
    when(rs.getObject(columnName)).thenReturn(testValue);
    when(rs.wasNull()).thenReturn(false);

    Optional<String> result = handler.getNullableResult(rs, columnName);

    assertTrue(result.isPresent());
    assertEquals(testValue, result.get());
  }

  @Test
  @DisplayName("ResultSet からインデックスで String 値を取得すると、Optional<String> に変換される")
  void testGetNullableResultByColumnIndexWithString() throws SQLException {
    OptionalTypeHandler<String> handler = new OptionalTypeHandler<>();
    handler.init(new String[0]);
    ResultSet rs = mock(ResultSet.class);
    int columnIndex = 1;
    String testValue = "test value";
    when(rs.getObject(columnIndex)).thenReturn(testValue);
    when(rs.wasNull()).thenReturn(false);

    Optional<String> result = handler.getNullableResult(rs, columnIndex);

    assertTrue(result.isPresent());
    assertEquals(testValue, result.get());
  }

  @Test
  @DisplayName("CallableStatement から String 値を取得すると、Optional<String> に変換される")
  void testGetNullableResultFromCallableStatementWithString() throws SQLException {
    OptionalTypeHandler<String> handler = new OptionalTypeHandler<>();
    handler.init(new String[0]);
    CallableStatement cs = mock(CallableStatement.class);
    int columnIndex = 1;
    String testValue = "callable test";
    when(cs.getObject(columnIndex)).thenReturn(testValue);
    when(cs.wasNull()).thenReturn(false);

    Optional<String> result = handler.getNullableResult(cs, columnIndex);

    assertTrue(result.isPresent());
    assertEquals(testValue, result.get());
  }

  @Test
  @DisplayName("String を設定して取得すると、元の値が正しく復元される（往復変換）")
  void testRoundTripConversionWithString() throws SQLException {
    OptionalTypeHandler<String> handler = new OptionalTypeHandler<>();
    handler.init(new String[0]);
    PreparedStatement ps = mock(PreparedStatement.class);
    ResultSet rs = mock(ResultSet.class);
    String originalValue = "round trip test";
    Optional<String> original = Optional.of(originalValue);
    int parameterIndex = 1;

    handler.setNonNullParameter(ps, parameterIndex, original, JdbcType.VARCHAR);

    verify(ps).setString(parameterIndex, originalValue);

    when(rs.getObject(parameterIndex)).thenReturn(originalValue);
    when(rs.wasNull()).thenReturn(false);
    Optional<String> result = handler.getNullableResult(rs, parameterIndex);

    assertTrue(result.isPresent());
    assertEquals(originalValue, result.get());
  }

  @Test
  @DisplayName("Optional<Integer> に値がある場合、PreparedStatement に Integer として設定される")
  void testSetNonNullParameterWithIntegerValue() throws SQLException {
    OptionalTypeHandler<Integer> handler = new OptionalTypeHandler<>();
    PreparedStatement ps = mock(PreparedStatement.class);
    Integer testValue = 42;
    Optional<Integer> parameter = Optional.of(testValue);
    int parameterIndex = 1;

    handler.setNonNullParameter(ps, parameterIndex, parameter, JdbcType.INTEGER);

    verify(ps).setInt(parameterIndex, testValue);
  }

  @Test
  @DisplayName("Optional<BigDecimal> に値がある場合、PreparedStatement に BigDecimal として設定される")
  void testSetNonNullParameterWithBigDecimalValue() throws SQLException {
    OptionalTypeHandler<java.math.BigDecimal> handler = new OptionalTypeHandler<>();
    PreparedStatement ps = mock(PreparedStatement.class);
    java.math.BigDecimal testValue = new java.math.BigDecimal("123.45");
    Optional<java.math.BigDecimal> parameter = Optional.of(testValue);
    int parameterIndex = 1;

    handler.setNonNullParameter(ps, parameterIndex, parameter, JdbcType.NUMERIC);

    verify(ps).setBigDecimal(parameterIndex, testValue);
  }

  @Test
  @DisplayName("Optional<Long> に値がある場合、PreparedStatement に Long として設定される")
  void testSetNonNullParameterWithLongValue() throws SQLException {
    OptionalTypeHandler<Long> handler = new OptionalTypeHandler<>();
    PreparedStatement ps = mock(PreparedStatement.class);
    Long testValue = 9876543210L;
    Optional<Long> parameter = Optional.of(testValue);
    int parameterIndex = 1;

    handler.setNonNullParameter(ps, parameterIndex, parameter, JdbcType.BIGINT);

    verify(ps).setLong(parameterIndex, testValue);
  }

  @Test
  @DisplayName("Optional<Double> に値がある場合、PreparedStatement に Double として設定される")
  void testSetNonNullParameterWithDoubleValue() throws SQLException {
    OptionalTypeHandler<Double> handler = new OptionalTypeHandler<>();
    PreparedStatement ps = mock(PreparedStatement.class);
    Double testValue = 3.14159;
    Optional<Double> parameter = Optional.of(testValue);
    int parameterIndex = 1;

    handler.setNonNullParameter(ps, parameterIndex, parameter, JdbcType.DOUBLE);

    verify(ps).setDouble(parameterIndex, testValue);
  }

  @Test
  @DisplayName("Integer を設定して取得すると、元の値が正しく復元される（往復変換）")
  void testRoundTripConversionWithInteger() throws SQLException {
    OptionalTypeHandler<Integer> handler = new OptionalTypeHandler<>();
    handler.init(new Integer[0]);
    PreparedStatement ps = mock(PreparedStatement.class);
    ResultSet rs = mock(ResultSet.class);
    Integer originalValue = 999;
    Optional<Integer> original = Optional.of(originalValue);
    int parameterIndex = 1;

    handler.setNonNullParameter(ps, parameterIndex, original, JdbcType.INTEGER);

    verify(ps).setInt(parameterIndex, originalValue);

    when(rs.getObject(parameterIndex)).thenReturn(originalValue);
    when(rs.wasNull()).thenReturn(false);
    Optional<Integer> result = handler.getNullableResult(rs, parameterIndex);

    assertTrue(result.isPresent());
    assertEquals(originalValue, result.get());
  }

  @Test
  @DisplayName("BigDecimal を設定して取得すると、元の値が正しく復元される（往復変換）")
  void testRoundTripConversionWithBigDecimal() throws SQLException {
    OptionalTypeHandler<java.math.BigDecimal> handler = new OptionalTypeHandler<>();
    handler.init(new java.math.BigDecimal[0]);
    PreparedStatement ps = mock(PreparedStatement.class);
    ResultSet rs = mock(ResultSet.class);
    java.math.BigDecimal originalValue = new java.math.BigDecimal("9999.99");
    Optional<java.math.BigDecimal> original = Optional.of(originalValue);
    int parameterIndex = 1;

    handler.setNonNullParameter(ps, parameterIndex, original, JdbcType.NUMERIC);

    verify(ps).setBigDecimal(parameterIndex, originalValue);

    when(rs.getObject(parameterIndex)).thenReturn(originalValue);
    when(rs.wasNull()).thenReturn(false);
    Optional<java.math.BigDecimal> result = handler.getNullableResult(rs, parameterIndex);

    assertTrue(result.isPresent());
    assertEquals(originalValue, result.get());
  }

  @Test
  @DisplayName("Optional<Boolean> に true がある場合、PreparedStatement に Boolean として設定される")
  void testSetNonNullParameterWithBooleanTrueValue() throws SQLException {
    OptionalTypeHandler<Boolean> handler = new OptionalTypeHandler<>();
    PreparedStatement ps = mock(PreparedStatement.class);
    Boolean testValue = true;
    Optional<Boolean> parameter = Optional.of(testValue);
    int parameterIndex = 1;

    handler.setNonNullParameter(ps, parameterIndex, parameter, JdbcType.BIT);

    verify(ps).setBoolean(parameterIndex, testValue);
  }

  @Test
  @DisplayName("Optional<Boolean> に false がある場合、PreparedStatement に Boolean として設定される")
  void testSetNonNullParameterWithBooleanFalseValue() throws SQLException {
    OptionalTypeHandler<Boolean> handler = new OptionalTypeHandler<>();
    PreparedStatement ps = mock(PreparedStatement.class);
    Boolean testValue = false;
    Optional<Boolean> parameter = Optional.of(testValue);
    int parameterIndex = 1;

    handler.setNonNullParameter(ps, parameterIndex, parameter, JdbcType.BIT);

    verify(ps).setBoolean(parameterIndex, testValue);
  }

  @Test
  @DisplayName("Boolean を設定して取得すると、元の値が正しく復元される（往復変換）")
  void testRoundTripConversionWithBoolean() throws SQLException {
    OptionalTypeHandler<Boolean> handler = new OptionalTypeHandler<>();
    handler.init(new Boolean[0]);
    PreparedStatement ps = mock(PreparedStatement.class);
    ResultSet rs = mock(ResultSet.class);
    Boolean originalValue = true;
    Optional<Boolean> original = Optional.of(originalValue);
    int parameterIndex = 1;

    handler.setNonNullParameter(ps, parameterIndex, original, JdbcType.BIT);

    verify(ps).setBoolean(parameterIndex, originalValue);

    when(rs.getObject(parameterIndex)).thenReturn(originalValue);
    when(rs.wasNull()).thenReturn(false);
    Optional<Boolean> result = handler.getNullableResult(rs, parameterIndex);

    assertTrue(result.isPresent());
    assertEquals(originalValue, result.get());
  }
}
