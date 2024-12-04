package com.tierline.mybatis.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/** MyBatis type handler for {@link Optional} of {@link OffsetDateTime}. */
public class OptionalTimestampTypeHandler extends BaseTypeHandler<Optional<OffsetDateTime>> {

  @Override
  public void setNonNullParameter(
      PreparedStatement ps, int i, Optional<OffsetDateTime> parameter, JdbcType jdbcType)
      throws SQLException {
    if (parameter.isPresent()) {
      ps.setTimestamp(i, Timestamp.from(parameter.get().toInstant()));
    } else {
      ps.setNull(i, Types.TIMESTAMP);
    }
  }

  @Override
  public Optional<OffsetDateTime> getNullableResult(ResultSet rs, String columnName)
      throws SQLException {
    var timestamp = rs.getTimestamp(columnName);
    return toOptionalOffsetDateTime(timestamp);
  }

  @Override
  public Optional<OffsetDateTime> getNullableResult(ResultSet rs, int columnIndex)
      throws SQLException {
    var timestamp = rs.getTimestamp(columnIndex);
    return toOptionalOffsetDateTime(timestamp);
  }

  @Override
  public Optional<OffsetDateTime> getNullableResult(CallableStatement cs, int columnIndex)
      throws SQLException {
    var timestamp = cs.getTimestamp(columnIndex);
    return toOptionalOffsetDateTime(timestamp);
  }

  private Optional<OffsetDateTime> toOptionalOffsetDateTime(Timestamp timestamp) {
    if (timestamp == null) {
      return Optional.empty();
    }
    var offsetDateTime = timestamp.toInstant().atOffset(ZoneOffset.ofHours(9));
    return Optional.of(offsetDateTime);
  }
}
