package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.converter.Context;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class BlobPreparedStatementIndexSetter implements PreparedStatementIndexSetter<Blob> {
    @Override
    public void set(PreparedStatement target, Blob value, int columnIndex, Context context) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.BINARY);
        } else {
            target.setBlob(columnIndex, value);
        }
    }
}
