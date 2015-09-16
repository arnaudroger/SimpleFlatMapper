package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.LongSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class LongPreparedStatementSetter implements Setter<PreparedStatement, Long>, LongSetter<PreparedStatement> {
    private final int columnIndex;

    public LongPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void set(PreparedStatement target, Long value) throws SQLException {
        if (value != null) {
            target.setLong(columnIndex, value);
        } else {
            target.setNull(columnIndex, Types.BIGINT);
        }
    }

    @Override
    public void setLong(PreparedStatement target, long value) throws Exception {
        target.setLong(columnIndex, value);
    }
}
