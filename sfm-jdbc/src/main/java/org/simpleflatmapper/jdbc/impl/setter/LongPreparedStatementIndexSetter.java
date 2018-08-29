package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.converter.Context;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class LongPreparedStatementIndexSetter implements PreparedStatementIndexSetter<Long> {

    @Override
    public void set(PreparedStatement target, Long value, int columnIndex, Context context) throws SQLException {
        if (value != null) {
            target.setLong(columnIndex, value);
        } else {
            target.setNull(columnIndex, Types.BIGINT);
        }
    }

    public void setLong(PreparedStatement target, long value, int columnIndex, Context context) throws Exception {
        target.setLong(columnIndex, value);
    }
}
