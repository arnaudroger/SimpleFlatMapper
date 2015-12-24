package org.sfm.jdbc.impl.setter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class ShortPreparedStatementIndexedSetter implements PrepareStatementIndexedSetter<Short> {
    @Override
    public void set(PreparedStatement target, Short value, int columnIndex) throws SQLException {
        if (value != null) {
            target.setShort(columnIndex, value);
        } else {
            target.setNull(columnIndex, Types.SMALLINT);
        }
    }

    public void setShort(PreparedStatement target, short value, int columnIndex) throws Exception {
        target.setShort(columnIndex, value);
    }
}
