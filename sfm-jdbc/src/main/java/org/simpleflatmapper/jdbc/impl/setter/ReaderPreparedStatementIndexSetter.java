package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.converter.Context;

import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class ReaderPreparedStatementIndexSetter implements PreparedStatementIndexSetter<Reader> {
    @Override
    public void set(PreparedStatement target, Reader value, int columnIndex, Context context) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.VARCHAR);
        } else {
            target.setCharacterStream(columnIndex, value);
        }
    }
}
