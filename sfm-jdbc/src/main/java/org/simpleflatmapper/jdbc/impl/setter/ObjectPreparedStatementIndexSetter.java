package org.simpleflatmapper.jdbc.impl.setter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class ObjectPreparedStatementIndexSetter implements PreparedStatementIndexSetter<Object> {

    @Override
    public void set(PreparedStatement target, Object value, int columnIndex) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.JAVA_OBJECT);
        } else {
            target.setObject(columnIndex, value);
        }
    }
}
