package com.tierline.mybatis.typehandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.Optional;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Test class for {@link OptionalLocalDateTypeHandler}. */
@DisplayName("OptionalLocalDateTypeHandler のテスト")
class OptionalLocalDateTypeHandlerTest {

  private OptionalLocalDateTypeHandler handler;

  @BeforeEach
  void setUp() {
    handler = new OptionalLocalDateTypeHandler();
  }

  @Test
  @DisplayName("Optional に値がある場合、PreparedStatement に java.sql.Date として設定される")
  void testSetNonNullParameterWithPresentValue() throws SQLException {
    PreparedStatement ps = mock(PreparedStatement.class);
    LocalDate testDate = LocalDate.of(2024, 1, 15);
    Optional<LocalDate> parameter = Optional.of(testDate);
    int parameterIndex = 1;

    handler.setNonNullParameter(ps, parameterIndex, parameter, JdbcType.DATE);

    verify(ps).setDate(parameterIndex, Date.valueOf(testDate));
  }

  @Test
  @DisplayName("Optional が空の場合、PreparedStatement に NULL が設定される")
  void testSetNonNullParameterWithEmptyValue() throws SQLException {
    PreparedStatement ps = mock(PreparedStatement.class);
    Optional<LocalDate> parameter = Optional.empty();
    int parameterIndex = 1;

    handler.setNonNullParameter(ps, parameterIndex, parameter, JdbcType.DATE);

    verify(ps).setNull(parameterIndex, Types.DATE);
  }

  @Test
  @DisplayName("ResultSet からカラム名で非NULL値を取得すると、Optional<LocalDate> に変換される")
  void testGetNullableResultByColumnNameWithNonNullValue() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    String columnName = "test_date";
    LocalDate testDate = LocalDate.of(2024, 1, 15);
    Date sqlDate = Date.valueOf(testDate);
    when(rs.getDate(columnName)).thenReturn(sqlDate);

    Optional<LocalDate> result = handler.getNullableResult(rs, columnName);

    assertTrue(result.isPresent());
    assertEquals(testDate, result.get());
  }

  @Test
  @DisplayName("ResultSet からカラム名でNULL値を取得すると、Optional.empty() が返される")
  void testGetNullableResultByColumnNameWithNullValue() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    String columnName = "test_date";
    when(rs.getDate(columnName)).thenReturn(null);

    Optional<LocalDate> result = handler.getNullableResult(rs, columnName);

    assertFalse(result.isPresent());
  }

  @Test
  @DisplayName("ResultSet からインデックスで非NULL値を取得すると、Optional<LocalDate> に変換される")
  void testGetNullableResultByColumnIndexWithNonNullValue() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    int columnIndex = 1;
    LocalDate testDate = LocalDate.of(2024, 12, 25);
    Date sqlDate = Date.valueOf(testDate);
    when(rs.getDate(columnIndex)).thenReturn(sqlDate);

    Optional<LocalDate> result = handler.getNullableResult(rs, columnIndex);

    assertTrue(result.isPresent());
    assertEquals(testDate, result.get());
  }

  @Test
  @DisplayName("ResultSet からインデックスでNULL値を取得すると、Optional.empty() が返される")
  void testGetNullableResultByColumnIndexWithNullValue() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    int columnIndex = 1;
    when(rs.getDate(columnIndex)).thenReturn(null);

    Optional<LocalDate> result = handler.getNullableResult(rs, columnIndex);

    assertFalse(result.isPresent());
  }

  @Test
  @DisplayName("CallableStatement から非NULL値を取得すると、Optional<LocalDate> に変換される")
  void testGetNullableResultFromCallableStatementWithNonNullValue() throws SQLException {
    CallableStatement cs = mock(CallableStatement.class);
    int columnIndex = 1;
    LocalDate testDate = LocalDate.of(2024, 6, 30);
    Date sqlDate = Date.valueOf(testDate);
    when(cs.getDate(columnIndex)).thenReturn(sqlDate);

    Optional<LocalDate> result = handler.getNullableResult(cs, columnIndex);

    assertTrue(result.isPresent());
    assertEquals(testDate, result.get());
  }

  @Test
  @DisplayName("CallableStatement からNULL値を取得すると、Optional.empty() が返される")
  void testGetNullableResultFromCallableStatementWithNullValue() throws SQLException {
    CallableStatement cs = mock(CallableStatement.class);
    int columnIndex = 1;
    when(cs.getDate(columnIndex)).thenReturn(null);

    Optional<LocalDate> result = handler.getNullableResult(cs, columnIndex);

    assertFalse(result.isPresent());
  }

  @Test
  @DisplayName("LocalDate を設定して取得すると、元の値が正しく復元される（往復変換）")
  void testRoundTripConversion() throws SQLException {
    PreparedStatement ps = mock(PreparedStatement.class);
    ResultSet rs = mock(ResultSet.class);
    LocalDate originalDate = LocalDate.of(2024, 3, 14);
    Optional<LocalDate> original = Optional.of(originalDate);
    int parameterIndex = 1;
    Date sqlDate = Date.valueOf(originalDate);

    handler.setNonNullParameter(ps, parameterIndex, original, JdbcType.DATE);

    verify(ps).setDate(parameterIndex, sqlDate);

    when(rs.getDate(parameterIndex)).thenReturn(sqlDate);
    Optional<LocalDate> result = handler.getNullableResult(rs, parameterIndex);

    assertTrue(result.isPresent());
    assertEquals(originalDate, result.get());
  }
}
