package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class BytesPreparedStatementSetter implements Setter<PreparedStatement, byte[]> {
    private final int columnIndex;

    public BytesPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void set(PreparedStatement target, byte[] value) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.BINARY);
        } else {
            target.setBytes(columnIndex, value);
        }
    }
}
