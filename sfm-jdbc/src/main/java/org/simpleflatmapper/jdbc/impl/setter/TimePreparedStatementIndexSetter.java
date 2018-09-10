package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.converter.Context;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;

public class TimePreparedStatementIndexSetter implements PreparedStatementIndexSetter<Time> {
    @Override
    public void set(PreparedStatement ps, Time value, int columnIndex, Context context) throws SQLException {
        if (value == null) {
            ps.setNull(columnIndex, Types.TIME);
        } else {
            ps.setTime(columnIndex, value);
        }
    }
}
