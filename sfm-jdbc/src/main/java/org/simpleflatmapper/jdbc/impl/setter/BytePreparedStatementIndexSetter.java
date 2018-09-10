package org.simpleflatmapper.jdbc.impl.setter;


import org.simpleflatmapper.converter.Context;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class BytePreparedStatementIndexSetter implements PreparedStatementIndexSetter<Byte> {

    @Override
    public void set(PreparedStatement target, Byte value, int columnIndex, Context context) throws SQLException {
        if (value != null) {
            target.setByte(columnIndex, value);
        } else {
            target.setNull(columnIndex, Types.TINYINT);
        }
    }

    public void setByte(PreparedStatement target, byte value, int columnIndex, Context context) throws Exception {
        target.setByte(columnIndex, value);
    }
}
