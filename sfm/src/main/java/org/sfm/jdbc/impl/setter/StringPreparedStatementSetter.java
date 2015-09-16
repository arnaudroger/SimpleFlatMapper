package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StringPreparedStatementSetter implements Setter<PreparedStatement, String> {
    private final int columnIndex;

    public StringPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void set(PreparedStatement target, String value) throws SQLException {
        target.setString(columnIndex, value);
    }
}
