package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;

import java.sql.PreparedStatement;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.Types;

public class RowIdPreparedStatementIndexedSetter implements PrepareStatementIndexedSetter<RowId> {
    @Override
    public void set(PreparedStatement target, RowId value, int columnIndex) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.ROWID);
        } else {
            target.setRowId(columnIndex, value);
        }
    }
}
