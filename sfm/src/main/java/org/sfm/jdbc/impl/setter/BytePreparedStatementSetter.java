package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.ByteSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class BytePreparedStatementSetter implements Setter<PreparedStatement, Byte>, ByteSetter<PreparedStatement> {
    private final int columnIndex;

    public BytePreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void set(PreparedStatement target, Byte value) throws SQLException {
        if (value != null) {
            target.setByte(columnIndex, value);
        } else {
            target.setNull(columnIndex, Types.TINYINT);
        }
    }

    @Override
    public void setByte(PreparedStatement target, byte value) throws Exception {
        target.setByte(columnIndex, value);
    }
}
