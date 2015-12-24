package org.sfm.jdbc.impl.setter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class IntegerPreparedStatementIndexedSetter implements PrepareStatementIndexedSetter<Integer> {

    @Override
    public void set(PreparedStatement target, Integer value, int columnIndex) throws SQLException {
        if (value != null) {
            target.setInt(columnIndex, value);
        } else {
            target.setNull(columnIndex, Types.INTEGER);
        }
    }

    public void setInt(PreparedStatement target, int value, int columnIndex) throws Exception {
        target.setInt(columnIndex, value);
    }
}
