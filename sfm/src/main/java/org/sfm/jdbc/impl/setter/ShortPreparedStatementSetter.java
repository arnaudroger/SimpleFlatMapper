package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.ShortSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class ShortPreparedStatementSetter implements Setter<PreparedStatement, Short>, ShortSetter<PreparedStatement> {
    private final int columnIndex;

    public ShortPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void set(PreparedStatement target, Short value) throws SQLException {
        if (value != null) {
            target.setShort(columnIndex, value);
        } else {
            target.setNull(columnIndex, Types.SMALLINT);
        }
    }

    @Override
    public void setShort(PreparedStatement target, short value) throws Exception {
        target.setShort(columnIndex, value);
    }
}
