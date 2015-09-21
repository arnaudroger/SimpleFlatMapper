package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;

import java.sql.*;

public class DatePreparedStatementSetter implements Setter<PreparedStatement, Date> {
    private final int columnIndex;

    public DatePreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void set(PreparedStatement target, Date value) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.DATE);
        } else {
            target.setDate(columnIndex, value);
        }
    }
}
