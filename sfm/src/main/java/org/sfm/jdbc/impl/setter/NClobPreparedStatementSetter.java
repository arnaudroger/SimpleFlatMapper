package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;

import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class NClobPreparedStatementSetter implements Setter<PreparedStatement, NClob> {
    private final int columnIndex;

    public NClobPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void set(PreparedStatement target, NClob value) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.NCLOB);
        } else {
            target.setNClob(columnIndex, value);
        }
    }
}
