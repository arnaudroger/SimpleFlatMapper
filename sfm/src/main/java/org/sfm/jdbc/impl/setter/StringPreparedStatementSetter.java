package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class StringPreparedStatementSetter implements Setter<PreparedStatement, String> {
    private final int columnIndex;

    public StringPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void set(PreparedStatement target, String value) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.VARCHAR);
        } else {
            target.setString(columnIndex, value);
        }
    }
}
