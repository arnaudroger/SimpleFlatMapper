package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;

import java.sql.Ref;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class RefPreparedStatementSetter implements Setter<PreparedStatement, Ref> {
    private final int columnIndex;

    public RefPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void set(PreparedStatement target, Ref value) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.REF);
        } else {
            target.setRef(columnIndex, value);
        }
    }
}
