package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.IntSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class IntegerPreparedStatementSetter implements Setter<PreparedStatement, Integer>, IntSetter<PreparedStatement> {
    private final int columnIndex;

    public IntegerPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void set(PreparedStatement target, Integer value) throws SQLException {
        if (value != null) {
            target.setInt(columnIndex, value);
        } else {
            target.setNull(columnIndex, Types.INTEGER);
        }
    }

    @Override
    public void setInt(PreparedStatement target, int value) throws Exception {
        target.setInt(columnIndex, value);
    }
}
