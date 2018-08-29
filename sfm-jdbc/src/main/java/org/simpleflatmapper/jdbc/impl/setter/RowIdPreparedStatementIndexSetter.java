package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.converter.Context;

import java.sql.PreparedStatement;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.Types;

public class RowIdPreparedStatementIndexSetter implements PreparedStatementIndexSetter<RowId> {
    @Override
    public void set(PreparedStatement target, RowId value, int columnIndex, Context context) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.ROWID);
        } else {
            target.setRowId(columnIndex, value);
        }
    }
}
