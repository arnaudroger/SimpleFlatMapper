package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.converter.Context;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class InputStreamPreparedStatementIndexSetter implements PreparedStatementIndexSetter<InputStream> {

    @Override
    public void set(PreparedStatement target, InputStream value, int columnIndex, Context context) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.BINARY);
        } else {
            target.setBinaryStream(columnIndex, value);
        }
    }
}
