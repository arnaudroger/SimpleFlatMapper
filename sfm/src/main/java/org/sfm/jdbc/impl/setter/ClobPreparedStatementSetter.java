package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;

import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class ClobPreparedStatementSetter implements Setter<PreparedStatement, Clob> {
    private final int columnIndex;

    public ClobPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void set(PreparedStatement target, Clob value) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.CLOB);
        } else {
            target.setClob(columnIndex, value);
        }
    }
}
