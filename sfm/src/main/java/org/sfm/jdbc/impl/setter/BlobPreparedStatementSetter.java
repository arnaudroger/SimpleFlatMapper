package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class BlobPreparedStatementSetter implements Setter<PreparedStatement, Blob> {
    private final int columnIndex;

    public BlobPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void set(PreparedStatement target, Blob value) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.BINARY);
        } else {
            target.setBlob(columnIndex, value);
        }
    }
}
