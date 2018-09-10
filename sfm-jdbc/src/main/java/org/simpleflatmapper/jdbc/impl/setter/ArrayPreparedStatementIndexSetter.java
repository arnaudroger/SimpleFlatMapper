package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.converter.Context;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class ArrayPreparedStatementIndexSetter implements PreparedStatementIndexSetter<Array> {

    @Override
    public void set(PreparedStatement target, Array value, int columnIndex, Context context) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.ARRAY);
        } else {
            target.setArray(columnIndex, value);
        }
    }
}
