package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.BooleanSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class BooleanPreparedStatementSetter implements Setter<PreparedStatement, Boolean>, BooleanSetter<PreparedStatement> {
    private final int columnIndex;

    public BooleanPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void set(PreparedStatement target, Boolean value) throws SQLException {
        if (value != null) {
            target.setBoolean(columnIndex, value);
        } else {
            target.setNull(columnIndex, Types.BOOLEAN);
        }
    }

    @Override
    public void setBoolean(PreparedStatement target, boolean value) throws Exception {
        target.setBoolean(columnIndex, value);
    }
}
