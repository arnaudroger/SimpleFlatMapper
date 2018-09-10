package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.converter.Context;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class ShortPreparedStatementIndexSetter implements PreparedStatementIndexSetter<Short> {
    @Override
    public void set(PreparedStatement target, Short value, int columnIndex, Context context) throws SQLException {
        if (value != null) {
            target.setShort(columnIndex, value);
        } else {
            target.setNull(columnIndex, Types.SMALLINT);
        }
    }

    public void setShort(PreparedStatement target, short value, int columnIndex, Context context) throws Exception {
        target.setShort(columnIndex, value);
    }
}
