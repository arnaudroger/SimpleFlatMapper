package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.converter.Context;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class IntegerPreparedStatementIndexSetter implements PreparedStatementIndexSetter<Integer> {

    @Override
    public void set(PreparedStatement target, Integer value, int columnIndex, Context context) throws SQLException {
        if (value != null) {
            target.setInt(columnIndex, value);
        } else {
            target.setNull(columnIndex, Types.INTEGER);
        }
    }

    public void setInt(PreparedStatement target, int value, int columnIndex, Context context) throws Exception {
        target.setInt(columnIndex, value);
    }
}
