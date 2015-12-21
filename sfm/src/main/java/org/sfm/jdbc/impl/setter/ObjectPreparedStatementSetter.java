package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class ObjectPreparedStatementSetter implements Setter<PreparedStatement, Object> {
    private final int columnIndex;

    public ObjectPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void set(PreparedStatement target, Object value) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.JAVA_OBJECT);
        } else {
            target.setObject(columnIndex, value);
        }
    }
}
