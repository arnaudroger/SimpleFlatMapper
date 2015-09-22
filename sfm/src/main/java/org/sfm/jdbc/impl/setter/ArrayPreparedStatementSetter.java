package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class ArrayPreparedStatementSetter implements Setter<PreparedStatement, Array> {
    private final int columnIndex;

    public ArrayPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void set(PreparedStatement target, Array value) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.ARRAY);
        } else {
            target.setArray(columnIndex, value);
        }
    }
}
