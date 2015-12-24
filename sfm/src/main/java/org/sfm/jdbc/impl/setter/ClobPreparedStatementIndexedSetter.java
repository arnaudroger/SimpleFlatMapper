package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;

import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class ClobPreparedStatementIndexedSetter implements PrepareStatementIndexedSetter<Clob> {

    @Override
    public void set(PreparedStatement target, Clob value, int columnIndex) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.CLOB);
        } else {
            target.setClob(columnIndex, value);
        }
    }
}
