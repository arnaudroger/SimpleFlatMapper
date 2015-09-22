package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class InputStreamPreparedStatementSetter implements Setter<PreparedStatement, InputStream> {
    private final int columnIndex;

    public InputStreamPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void set(PreparedStatement target, InputStream value) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.BINARY);
        } else {
            target.setBinaryStream(columnIndex, value);
        }
    }
}
