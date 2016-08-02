package org.simpleflatmapper.jdbc.impl.setter;

import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class ReaderPreparedStatementIndexSetter implements PreparedStatementIndexSetter<Reader> {
    @Override
    public void set(PreparedStatement target, Reader value, int columnIndex) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.VARCHAR);
        } else {
            target.setCharacterStream(columnIndex, value);
        }
    }
}
