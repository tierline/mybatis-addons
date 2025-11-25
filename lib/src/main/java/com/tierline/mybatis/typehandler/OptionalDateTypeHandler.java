package com.tierline.mybatis.typehandler;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.Optional;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/** MyBatis type handler for {@link Optional} of {@link LocalDate}. */
public class OptionalDateTypeHandler extends BaseTypeHandler<Optional<LocalDate>> {

  @Override
  public void setNonNullParameter(
      PreparedStatement ps, int i, Optional<LocalDate> parameter, JdbcType jdbcType)
      throws SQLException {
    if (parameter.isPresent()) {
      ps.setDate(i, Date.valueOf(parameter.get()));
    } else {
      ps.setNull(i, Types.DATE);
    }
  }

  @Override
  public Optional<LocalDate> getNullableResult(ResultSet rs, String columnName)
      throws SQLException {
    var date = rs.getDate(columnName);
    return toOptionalLocalDate(date);
  }

  @Override
  public Optional<LocalDate> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    var date = rs.getDate(columnIndex);
    return toOptionalLocalDate(date);
  }

  @Override
  public Optional<LocalDate> getNullableResult(CallableStatement cs, int columnIndex)
      throws SQLException {
    var date = cs.getDate(columnIndex);
    return toOptionalLocalDate(date);
  }

  private Optional<LocalDate> toOptionalLocalDate(Date date) {
    if (date == null) {
      return Optional.empty();
    }
    return Optional.of(date.toLocalDate());
  }
}
