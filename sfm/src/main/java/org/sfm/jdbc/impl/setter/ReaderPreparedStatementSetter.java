package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;

import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class ReaderPreparedStatementSetter implements Setter<PreparedStatement, Reader> {
    private final int columnIndex;

    public ReaderPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void set(PreparedStatement target, Reader value) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.VARCHAR);
        } else {
            target.setCharacterStream(columnIndex, value);
        }
    }
}
