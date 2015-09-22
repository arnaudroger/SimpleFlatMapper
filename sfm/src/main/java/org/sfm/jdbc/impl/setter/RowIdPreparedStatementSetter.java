package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;

import java.sql.PreparedStatement;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.Types;

public class RowIdPreparedStatementSetter implements Setter<PreparedStatement, RowId> {
    private final int columnIndex;

    public RowIdPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void set(PreparedStatement target, RowId value) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.ROWID);
        } else {
            target.setRowId(columnIndex, value);
        }
    }
}
