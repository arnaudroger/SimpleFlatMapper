package org.simpleflatmapper.jdbc.impl.setter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class BytesPreparedStatementIndexSetter implements PreparedStatementIndexSetter<byte[]> {
    @Override
    public void set(PreparedStatement target, byte[] value, int columnIndex) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.BINARY);
        } else {
            target.setBytes(columnIndex, value);
        }
    }
}
