package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.converter.Context;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class BytesPreparedStatementIndexSetter implements PreparedStatementIndexSetter<byte[]> {
    @Override
    public void set(PreparedStatement target, byte[] value, int columnIndex, Context context) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.BINARY);
        } else {
            target.setBytes(columnIndex, value);
        }
    }
}
