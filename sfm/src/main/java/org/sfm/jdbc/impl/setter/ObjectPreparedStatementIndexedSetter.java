package org.sfm.jdbc.impl.setter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class ObjectPreparedStatementIndexedSetter implements PrepareStatementIndexedSetter<Object> {

    @Override
    public void set(PreparedStatement target, Object value, int columnIndex) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.JAVA_OBJECT);
        } else {
            target.setObject(columnIndex, value);
        }
    }
}
