package org.simpleflatmapper.jdbc.impl.setter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

public class TimestampPreparedStatementIndexSetter implements PreparedStatementIndexSetter<Timestamp> {
    @Override
    public void set(PreparedStatement target, Timestamp value, int columnIndex) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.TIMESTAMP);
        } else {
            target.setTimestamp(columnIndex, value);
        }
    }
}
