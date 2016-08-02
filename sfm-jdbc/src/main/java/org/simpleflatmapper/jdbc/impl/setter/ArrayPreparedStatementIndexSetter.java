package org.simpleflatmapper.jdbc.impl.setter;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class ArrayPreparedStatementIndexSetter implements PreparedStatementIndexSetter<Array> {

    @Override
    public void set(PreparedStatement target, Array value, int columnIndex) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.ARRAY);
        } else {
            target.setArray(columnIndex, value);
        }
    }
}
