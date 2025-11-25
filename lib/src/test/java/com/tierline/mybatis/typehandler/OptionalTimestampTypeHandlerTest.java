package com.tierline.mybatis.typehandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Test class for {@link OptionalTimestampTypeHandler}. */
@DisplayName("OptionalTimestampTypeHandler のテスト")
class OptionalTimestampTypeHandlerTest {

  private OptionalTimestampTypeHandler handler;

  @BeforeEach
  void setUp() {
    handler = new OptionalTimestampTypeHandler();
  }

  @Test
  @DisplayName("Optional に値がある場合、PreparedStatement に java.sql.Timestamp として設定される")
  void testSetNonNullParameterWithPresentValue() throws SQLException {
    PreparedStatement ps = mock(PreparedStatement.class);
    OffsetDateTime testDateTime =
        OffsetDateTime.of(2024, 1, 15, 10, 30, 45, 0, ZoneOffset.ofHours(9));
    Optional<OffsetDateTime> parameter = Optional.of(testDateTime);
    int parameterIndex = 1;

    handler.setNonNullParameter(ps, parameterIndex, parameter, JdbcType.TIMESTAMP);

    verify(ps).setTimestamp(parameterIndex, Timestamp.from(testDateTime.toInstant()));
  }

  @Test
  @DisplayName("Optional が空の場合、PreparedStatement に NULL が設定される")
  void testSetNonNullParameterWithEmptyValue() throws SQLException {
    PreparedStatement ps = mock(PreparedStatement.class);
    Optional<OffsetDateTime> parameter = Optional.empty();
    int parameterIndex = 1;

    handler.setNonNullParameter(ps, parameterIndex, parameter, JdbcType.TIMESTAMP);

    verify(ps).setNull(parameterIndex, Types.TIMESTAMP);
  }

  @Test
  @DisplayName("ResultSet からカラム名で非NULL値を取得すると、Optional<OffsetDateTime> に変換される")
  void testGetNullableResultByColumnNameWithNonNullValue() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    String columnName = "test_timestamp";
    OffsetDateTime testDateTime =
        OffsetDateTime.of(2024, 1, 15, 10, 30, 45, 0, ZoneOffset.ofHours(9));
    Timestamp sqlTimestamp = Timestamp.from(testDateTime.toInstant());
    when(rs.getTimestamp(columnName)).thenReturn(sqlTimestamp);

    Optional<OffsetDateTime> result = handler.getNullableResult(rs, columnName);

    assertTrue(result.isPresent());
    assertEquals(testDateTime, result.get());
  }

  @Test
  @DisplayName("ResultSet からカラム名でNULL値を取得すると、Optional.empty() が返される")
  void testGetNullableResultByColumnNameWithNullValue() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    String columnName = "test_timestamp";
    when(rs.getTimestamp(columnName)).thenReturn(null);

    Optional<OffsetDateTime> result = handler.getNullableResult(rs, columnName);

    assertFalse(result.isPresent());
  }

  @Test
  @DisplayName("ResultSet からインデックスで非NULL値を取得すると、Optional<OffsetDateTime> に変換される")
  void testGetNullableResultByColumnIndexWithNonNullValue() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    int columnIndex = 1;
    OffsetDateTime testDateTime =
        OffsetDateTime.of(2024, 12, 25, 23, 59, 59, 0, ZoneOffset.ofHours(9));
    Timestamp sqlTimestamp = Timestamp.from(testDateTime.toInstant());
    when(rs.getTimestamp(columnIndex)).thenReturn(sqlTimestamp);

    Optional<OffsetDateTime> result = handler.getNullableResult(rs, columnIndex);

    assertTrue(result.isPresent());
    assertEquals(testDateTime, result.get());
  }

  @Test
  @DisplayName("ResultSet からインデックスでNULL値を取得すると、Optional.empty() が返される")
  void testGetNullableResultByColumnIndexWithNullValue() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    int columnIndex = 1;
    when(rs.getTimestamp(columnIndex)).thenReturn(null);

    Optional<OffsetDateTime> result = handler.getNullableResult(rs, columnIndex);

    assertFalse(result.isPresent());
  }

  @Test
  @DisplayName("CallableStatement から非NULL値を取得すると、Optional<OffsetDateTime> に変換される")
  void testGetNullableResultFromCallableStatementWithNonNullValue() throws SQLException {
    CallableStatement cs = mock(CallableStatement.class);
    int columnIndex = 1;
    OffsetDateTime testDateTime =
        OffsetDateTime.of(2024, 6, 30, 12, 0, 0, 0, ZoneOffset.ofHours(9));
    Timestamp sqlTimestamp = Timestamp.from(testDateTime.toInstant());
    when(cs.getTimestamp(columnIndex)).thenReturn(sqlTimestamp);

    Optional<OffsetDateTime> result = handler.getNullableResult(cs, columnIndex);

    assertTrue(result.isPresent());
    assertEquals(testDateTime, result.get());
  }

  @Test
  @DisplayName("CallableStatement からNULL値を取得すると、Optional.empty() が返される")
  void testGetNullableResultFromCallableStatementWithNullValue() throws SQLException {
    CallableStatement cs = mock(CallableStatement.class);
    int columnIndex = 1;
    when(cs.getTimestamp(columnIndex)).thenReturn(null);

    Optional<OffsetDateTime> result = handler.getNullableResult(cs, columnIndex);

    assertFalse(result.isPresent());
  }

  @Test
  @DisplayName("OffsetDateTime を設定して取得すると、元の値が正しく復元される（往復変換）")
  void testRoundTripConversion() throws SQLException {
    PreparedStatement ps = mock(PreparedStatement.class);
    ResultSet rs = mock(ResultSet.class);
    OffsetDateTime originalDateTime =
        OffsetDateTime.of(2024, 3, 14, 15, 30, 0, 0, ZoneOffset.ofHours(9));
    Optional<OffsetDateTime> original = Optional.of(originalDateTime);
    int parameterIndex = 1;
    Timestamp sqlTimestamp = Timestamp.from(originalDateTime.toInstant());

    handler.setNonNullParameter(ps, parameterIndex, original, JdbcType.TIMESTAMP);

    verify(ps).setTimestamp(parameterIndex, sqlTimestamp);

    when(rs.getTimestamp(parameterIndex)).thenReturn(sqlTimestamp);
    Optional<OffsetDateTime> result = handler.getNullableResult(rs, parameterIndex);

    assertTrue(result.isPresent());
    assertEquals(originalDateTime, result.get());
  }
}
