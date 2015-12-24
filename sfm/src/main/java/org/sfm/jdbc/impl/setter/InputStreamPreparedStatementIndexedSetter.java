package org.sfm.jdbc.impl.setter;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class InputStreamPreparedStatementIndexedSetter implements PrepareStatementIndexedSetter<InputStream> {

    @Override
    public void set(PreparedStatement target, InputStream value, int columnIndex) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.BINARY);
        } else {
            target.setBinaryStream(columnIndex, value);
        }
    }
}
