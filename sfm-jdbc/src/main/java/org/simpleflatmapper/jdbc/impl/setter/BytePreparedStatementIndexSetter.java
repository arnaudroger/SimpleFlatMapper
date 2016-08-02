package org.simpleflatmapper.jdbc.impl.setter;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class BytePreparedStatementIndexSetter implements PreparedStatementIndexSetter<Byte> {

    @Override
    public void set(PreparedStatement target, Byte value, int columnIndex) throws SQLException {
        if (value != null) {
            target.setByte(columnIndex, value);
        } else {
            target.setNull(columnIndex, Types.TINYINT);
        }
    }

    public void setByte(PreparedStatement target, byte value, int columnIndex) throws Exception {
        target.setByte(columnIndex, value);
    }
}
