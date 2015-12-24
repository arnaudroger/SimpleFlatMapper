package org.sfm.jdbc.impl.setter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class LongPreparedStatementIndexedSetter implements PrepareStatementIndexedSetter<Long> {

    @Override
    public void set(PreparedStatement target, Long value, int columnIndex) throws SQLException {
        if (value != null) {
            target.setLong(columnIndex, value);
        } else {
            target.setNull(columnIndex, Types.BIGINT);
        }
    }

    public void setLong(PreparedStatement target, long value, int columnIndex) throws Exception {
        target.setLong(columnIndex, value);
    }
}
