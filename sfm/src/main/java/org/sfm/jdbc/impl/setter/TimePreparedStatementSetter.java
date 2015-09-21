package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;

public class TimePreparedStatementSetter implements Setter<PreparedStatement, Time> {
    private final int columnIndex;

    public TimePreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void set(PreparedStatement target, Time value) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.TIME);
        } else {
            target.setTime(columnIndex, value);
        }
    }
}
