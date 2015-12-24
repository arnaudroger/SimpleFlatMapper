package org.sfm.jdbc.impl.setter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class BytesPreparedStatementIndexedSetter implements  PrepareStatementIndexedSetter<byte[]> {
    @Override
    public void set(PreparedStatement target, byte[] value, int columnIndex) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.BINARY);
        } else {
            target.setBytes(columnIndex, value);
        }
    }
}
