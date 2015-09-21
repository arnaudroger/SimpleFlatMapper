package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

public class TimestampPreparedStatementSetter implements Setter<PreparedStatement, Timestamp> {
    private final int columnIndex;

    public TimestampPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void set(PreparedStatement target, Timestamp value) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.TIMESTAMP);
        } else {
            target.setTimestamp(columnIndex, value);
        }
    }
}
